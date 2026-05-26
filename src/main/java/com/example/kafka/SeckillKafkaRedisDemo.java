package com.example.kafka;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.TopicExistsException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 电商秒杀独立演示：
 *
 * 1. Redis 保存库存和已成功购买用户。
 * 2. 并发用户请求先写入 Kafka。
 * 3. Kafka 消费者使用 Redis Lua 脚本原子判断重复购买并扣减库存。
 * 4. 最终汇总结果用于证明成功订单不会超过库存。
 */
public class SeckillKafkaRedisDemo {

    private static final String KAFKA_BOOTSTRAP_SERVERS = "119.45.176.101:9092";
    private static final String REDIS_HOST = "119.45.176.101";
    private static final int REDIS_PORT = 6379;

    private static final String TOPIC = "seckill-order-demo";
    private static final String SKU_ID = "sku-1001";

    private static final int INITIAL_STOCK = 20;
    private static final int REQUEST_COUNT = 200;
    private static final int UNIQUE_USER_COUNT = 80;
    private static final int PRODUCER_THREADS = 16;
    private static final int CONSUMER_THREADS = 3;
    private static final long WAIT_PROCESS_TIMEOUT_MS = 30000L;

    private static final String STOCK_KEY = "demo:seckill:stock:" + SKU_ID;
    private static final String BUYER_KEY = "demo:seckill:buyers:" + SKU_ID;
    private static final String ORDER_KEY = "demo:seckill:orders:" + SKU_ID;

    /*
     * Redis 是库存和成功购买用户的事实来源。
     * 判断重复购买、判断库存、扣减库存、写入订单这几步放在同一个 Lua 脚本里执行，
     * 即使多个 Kafka 消费者并发处理消息，Redis 也会保证脚本执行的原子性。
     *
     * 返回值：
     *   1  下单成功
     *   0  库存不足
     *  -1  同一用户已经成功购买过该商品
     */
    private static final String DEDUCT_STOCK_SCRIPT =
            "if redis.call('SISMEMBER', KEYS[2], ARGV[1]) == 1 then\n" +
            "  return -1\n" +
            "end\n" +
            "local stock = tonumber(redis.call('GET', KEYS[1]) or '0')\n" +
            "if stock <= 0 then\n" +
            "  return 0\n" +
            "end\n" +
            "redis.call('DECR', KEYS[1])\n" +
            "redis.call('SADD', KEYS[2], ARGV[1])\n" +
            "redis.call('RPUSH', KEYS[3], ARGV[2])\n" +
            "return 1";

    private final AtomicBoolean running = new AtomicBoolean(true);
    private final AtomicInteger sent = new AtomicInteger();
    private final AtomicInteger sendFailed = new AtomicInteger();
    private final AtomicInteger processed = new AtomicInteger();
    private final AtomicInteger success = new AtomicInteger();
    private final AtomicInteger duplicate = new AtomicInteger();
    private final AtomicInteger soldOut = new AtomicInteger();

    public static void main(String[] args) throws Exception {
        SeckillKafkaRedisDemo demo = new SeckillKafkaRedisDemo();
        demo.run();
    }

    private void run() throws Exception {
        String runId = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        System.out.println("运行批次：" + runId);
        System.out.println("Kafka 地址：" + KAFKA_BOOTSTRAP_SERVERS);
        System.out.println("Redis 地址：" + REDIS_HOST + ":" + REDIS_PORT);

        // 准备演示环境，并重置本次演示使用的 Redis 数据。
        ensureTopic();
        initRedis();

        // 先启动消费者，再发送秒杀请求。Kafka 承接瞬时流量，
        // 消费者按照自己的处理能力慢慢消费。
        ExecutorService consumerPool = Executors.newFixedThreadPool(CONSUMER_THREADS);
        for (int i = 0; i < CONSUMER_THREADS; i++) {
            final int consumerNo = i + 1;
            consumerPool.submit(new Runnable() {
                @Override
                public void run() {
                    consume(runId, consumerNo);
                }
            });
        }

        Thread.sleep(1200L);

        // 模拟大量用户同时点击秒杀按钮。
        sendRequests(runId);

        // 等待所有成功发送到 Kafka 的消息被消费，再打印 Redis 最终状态验证结果。
        waitUntilProcessed();

        running.set(false);
        consumerPool.shutdown();
        if (!consumerPool.awaitTermination(5, TimeUnit.SECONDS)) {
            consumerPool.shutdownNow();
        }

        printSummary();
    }

    private void ensureTopic() {
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BOOTSTRAP_SERVERS);
        props.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, "3000");
        props.put(AdminClientConfig.DEFAULT_API_TIMEOUT_MS_CONFIG, "5000");

        try (AdminClient admin = AdminClient.create(props)) {
            NewTopic topic = new NewTopic(TOPIC, 3, (short) 1);
            admin.createTopics(Collections.singletonList(topic)).all().get(5, TimeUnit.SECONDS);
            System.out.println("已创建 Kafka 主题：" + TOPIC);
        } catch (Exception e) {
            Throwable cause = e.getCause();
            if (cause instanceof TopicExistsException || e instanceof TopicExistsException) {
                System.out.println("Kafka 主题已存在：" + TOPIC);
            } else {
                System.out.println("跳过 Kafka 主题创建：" + e.getMessage());
            }
        }
    }

    private void initRedis() throws IOException {
        try (RedisClient redis = new RedisClient(REDIS_HOST, REDIS_PORT, 3000)) {
            // 这些 key 只用于演示，先清理可以保证每次运行结果可重复。
            redis.command("DEL", STOCK_KEY, BUYER_KEY, ORDER_KEY);
            redis.command("SET", STOCK_KEY, String.valueOf(INITIAL_STOCK));
        }
        System.out.println("Redis 初始化完成，库存=" + INITIAL_STOCK);
    }

    private void sendRequests(String runId) throws Exception {
        ExecutorService requestPool = Executors.newFixedThreadPool(PRODUCER_THREADS);
        final KafkaProducer<String, String> producer = new KafkaProducer<String, String>(producerProps());

        for (int i = 0; i < REQUEST_COUNT; i++) {
            final int index = i;
            requestPool.submit(new Runnable() {
                @Override
                public void run() {
                    // 故意让部分请求使用相同 userId，用于演示“一人一单”规则。
                    SeckillRequest request = SeckillRequest.of(runId, index);
                    ProducerRecord<String, String> record =
                            new ProducerRecord<String, String>(TOPIC, request.getSkuId(), request.encode());
                    try {
                        producer.send(record).get(10, TimeUnit.SECONDS);
                        sent.incrementAndGet();
                    } catch (Exception e) {
                        sendFailed.incrementAndGet();
                        System.err.println("Kafka 发送失败，请求ID=" + request.getRequestId()
                                + "，错误=" + e.getMessage());
                        if (e instanceof InterruptedException) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }
            });
        }

        requestPool.shutdown();
        if (!requestPool.awaitTermination(60, TimeUnit.SECONDS)) {
            requestPool.shutdownNow();
        }
        producer.flush();
        producer.close();

        System.out.println("Kafka 发送完成，成功=" + sent.get() + "，失败=" + sendFailed.get());
    }

    private void consume(String runId, int consumerNo) {
        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(consumerProps(runId, consumerNo));
        try (RedisClient redis = new RedisClient(REDIS_HOST, REDIS_PORT, 3000)) {
            consumer.subscribe(Collections.singletonList(TOPIC));
            while (running.get()) {
                ConsumerRecords<String, String> records = consumer.poll(500);
                for (ConsumerRecord<String, String> record : records) {
                    SeckillRequest request = SeckillRequest.parse(record.value());

                    // topic 中可能存在历史演示消息，runId 用来隔离本次运行。
                    if (!runId.equals(request.getRunId())) {
                        continue;
                    }
                    handleRequest(redis, request, consumerNo);
                }
                if (!records.isEmpty()) {
                    // Redis 处理完成后再提交 offset。真实订单系统里，创建订单也要做幂等。
                    consumer.commitAsync();
                }
            }
        } catch (Exception e) {
            if (running.get()) {
                System.err.println("消费者-" + consumerNo + " 已停止：" + e.getMessage());
            }
        } finally {
            consumer.close();
        }
    }

    private void handleRequest(RedisClient redis, SeckillRequest request, int consumerNo) throws IOException {
        String order = "订单ID=order-" + request.getRequestId()
                + "，用户ID=" + request.getUserId()
                + "，商品ID=" + request.getSkuId();

        // Redis Lua 脚本的原子返回值决定本次请求的业务结果。
        long result = redis.evalNumber(
                DEDUCT_STOCK_SCRIPT,
                3,
                STOCK_KEY,
                BUYER_KEY,
                ORDER_KEY,
                request.getUserId(),
                order
        );

        processed.incrementAndGet();
        if (result == 1L) {
            int count = success.incrementAndGet();
            System.out.println("下单成功 消费者-" + consumerNo + " " + order + "，成功数=" + count);
        } else if (result == -1L) {
            int count = duplicate.incrementAndGet();
            if (count <= 5) {
                System.out.println("重复下单 用户ID=" + request.getUserId()
                        + "，请求ID=" + request.getRequestId());
            }
        } else {
            int count = soldOut.incrementAndGet();
            if (count <= 5) {
                System.out.println("库存不足 用户ID=" + request.getUserId()
                        + "，请求ID=" + request.getRequestId());
            }
        }
    }

    private void waitUntilProcessed() throws InterruptedException {
        long deadline = System.currentTimeMillis() + WAIT_PROCESS_TIMEOUT_MS;
        while (System.currentTimeMillis() < deadline && processed.get() < sent.get()) {
            Thread.sleep(200L);
        }
        if (processed.get() < sent.get()) {
            System.out.println("等待消费超时，已发送=" + sent.get() + "，已处理=" + processed.get());
        }
    }

    private void printSummary() throws IOException {
        try (RedisClient redis = new RedisClient(REDIS_HOST, REDIS_PORT, 3000)) {
            // 直接读取 Redis，确保汇总结果反映真实最终状态，而不只是本地计数器。
            String stockLeft = redis.get(STOCK_KEY);
            long orderCount = redis.llen(ORDER_KEY);
            List<Object> orders = redis.lrange(ORDER_KEY, 0, -1);

            System.out.println();
            System.out.println("========== 秒杀汇总 ==========");
            System.out.println("配置请求数：        " + REQUEST_COUNT);
            System.out.println("Kafka 发送成功：    " + sent.get());
            System.out.println("Kafka 发送失败：    " + sendFailed.get());
            System.out.println("Kafka 已消费：      " + processed.get());
            System.out.println("下单成功：          " + success.get());
            System.out.println("重复下单拦截：      " + duplicate.get());
            System.out.println("库存不足拦截：      " + soldOut.get());
            System.out.println("Redis 剩余库存：    " + stockLeft);
            System.out.println("Redis 订单数量：    " + orderCount);
            System.out.println("校验结论：成功订单数 <= 初始库存，Redis 用户集合中无重复购买用户");
            System.out.println("前几条订单：");
            for (int i = 0; i < Math.min(orders.size(), 10); i++) {
                System.out.println("  " + orders.get(i));
            }
            System.out.println("=====================================");
        }
    }

    private Properties producerProps() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.RETRIES_CONFIG, "3");
        props.put(ProducerConfig.LINGER_MS_CONFIG, "5");
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, "32768");

        // 幂等生产者可以减少生产端重试带来的重复写入。
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
        props.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, "5000");
        props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, "5000");
        props.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, "20000");
        return props;
    }

    private Properties consumerProps(String runId, int consumerNo) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BOOTSTRAP_SERVERS);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "seckill-demo-" + runId);
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, "seckill-consumer-" + consumerNo + "-" + runId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "100");
        return props;
    }

    private static class SeckillRequest {
        private final String runId;
        private final String requestId;
        private final String skuId;
        private final String userId;
        private final long requestTime;

        private SeckillRequest(String runId, String requestId, String skuId, String userId, long requestTime) {
            this.runId = runId;
            this.requestId = requestId;
            this.skuId = skuId;
            this.userId = userId;
            this.requestTime = requestTime;
        }

        static SeckillRequest of(String runId, int index) {
            String requestId = runId + "-" + index;

            // 200 个请求共用 80 个用户，因此演示里会自然出现重复点击。
            String userId = "user-" + (index % UNIQUE_USER_COUNT);
            return new SeckillRequest(runId, requestId, SKU_ID, userId, System.currentTimeMillis());
        }

        static SeckillRequest parse(String value) {
            String[] parts = value.split("\\|", -1);
            if (parts.length != 5) {
                throw new IllegalArgumentException("Bad request message: " + value);
            }
            return new SeckillRequest(parts[0], parts[1], parts[2], parts[3], Long.parseLong(parts[4]));
        }

        String encode() {
            return runId + "|" + requestId + "|" + skuId + "|" + userId + "|" + requestTime;
        }

        String getRunId() {
            return runId;
        }

        String getRequestId() {
            return requestId;
        }

        String getSkuId() {
            return skuId;
        }

        String getUserId() {
            return userId;
        }
    }

    private static class RedisClient implements Closeable {
        private final Socket socket;
        private final InputStream input;
        private final OutputStream output;

        RedisClient(String host, int port, int timeoutMillis) throws IOException {
            this.socket = new Socket();
            this.socket.connect(new InetSocketAddress(host, port), timeoutMillis);
            this.socket.setSoTimeout(timeoutMillis);
            this.input = socket.getInputStream();
            this.output = socket.getOutputStream();
        }

        synchronized Object command(String... args) throws IOException {
            // 这里实现最小 RESP 协议，避免为了独立演示额外增加 Redis 客户端依赖。
            writeCommand(args);
            output.flush();
            return readReply(input);
        }

        long evalNumber(String script, int keyCount, String... keysAndArgs) throws IOException {
            String[] args = new String[3 + keysAndArgs.length];
            args[0] = "EVAL";
            args[1] = script;
            args[2] = String.valueOf(keyCount);
            System.arraycopy(keysAndArgs, 0, args, 3, keysAndArgs.length);
            Object reply = command(args);
            if (reply instanceof Long) {
                return (Long) reply;
            }
            return Long.parseLong(String.valueOf(reply));
        }

        String get(String key) throws IOException {
            Object reply = command("GET", key);
            return reply == null ? null : String.valueOf(reply);
        }

        long llen(String key) throws IOException {
            Object reply = command("LLEN", key);
            if (reply instanceof Long) {
                return (Long) reply;
            }
            return Long.parseLong(String.valueOf(reply));
        }

        List<Object> lrange(String key, int start, int stop) throws IOException {
            Object reply = command("LRANGE", key, String.valueOf(start), String.valueOf(stop));
            if (reply instanceof List) {
                return (List<Object>) reply;
            }
            return Collections.emptyList();
        }

        private void writeCommand(String[] args) throws IOException {
            writeAscii("*" + args.length + "\r\n");
            for (String arg : args) {
                byte[] data = arg.getBytes(StandardCharsets.UTF_8);
                writeAscii("$" + data.length + "\r\n");
                output.write(data);
                writeAscii("\r\n");
            }
        }

        private void writeAscii(String value) throws IOException {
            output.write(value.getBytes(StandardCharsets.US_ASCII));
        }

        private static Object readReply(InputStream input) throws IOException {
            int type = input.read();
            if (type == -1) {
                throw new EOFException("Redis closed connection");
            }
            if (type == '+') {
                return readLine(input);
            }
            if (type == '-') {
                throw new IOException("Redis error: " + readLine(input));
            }
            if (type == ':') {
                return Long.parseLong(readLine(input));
            }
            if (type == '$') {
                int length = Integer.parseInt(readLine(input));
                if (length == -1) {
                    return null;
                }
                byte[] data = readBytes(input, length);
                readCrlf(input);
                return new String(data, StandardCharsets.UTF_8);
            }
            if (type == '*') {
                int length = Integer.parseInt(readLine(input));
                if (length == -1) {
                    return null;
                }
                List<Object> values = new ArrayList<Object>(length);
                for (int i = 0; i < length; i++) {
                    values.add(readReply(input));
                }
                return values;
            }
            throw new IOException("Unsupported Redis reply type: " + (char) type);
        }

        private static String readLine(InputStream input) throws IOException {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            while (true) {
                int b = input.read();
                if (b == -1) {
                    throw new EOFException("Redis closed connection while reading line");
                }
                if (b == '\r') {
                    int next = input.read();
                    if (next != '\n') {
                        throw new IOException("Bad Redis line ending");
                    }
                    return new String(buffer.toByteArray(), StandardCharsets.UTF_8);
                }
                buffer.write(b);
            }
        }

        private static byte[] readBytes(InputStream input, int length) throws IOException {
            byte[] data = new byte[length];
            int offset = 0;
            while (offset < length) {
                int read = input.read(data, offset, length - offset);
                if (read == -1) {
                    throw new EOFException("Redis closed connection while reading bulk string");
                }
                offset += read;
            }
            return data;
        }

        private static void readCrlf(InputStream input) throws IOException {
            int cr = input.read();
            int lf = input.read();
            if (cr != '\r' || lf != '\n') {
                throw new IOException("Bad Redis bulk string ending");
            }
        }

        @Override
        public void close() throws IOException {
            socket.close();
        }
    }
}
