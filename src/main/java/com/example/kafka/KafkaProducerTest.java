package com.example.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.HashMap;
import java.util.Map;

public class KafkaProducerTest {
    public static void main(String[] args) {
        // 创建配置对象
        Map<String, Object> configMap = new HashMap<>();
        configMap.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        // 对生产的数据 K, V 进行序列化操作
        configMap.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        configMap.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        // 创建生产者对象
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(configMap);

        // 创建数据
        // 第一个参数：主题的名称
        // 第二个参数：数据的key
        // 第二个参数：数据的value
//        ProducerRecord<String, String> record = new ProducerRecord<>("test", "key", "value");

        for (int i = 0; i < 10; i++) {
            ProducerRecord<String, String> record = new ProducerRecord<>("test", "key" + i, "value" + i);
            producer.send(record);
        }


        // 关闭生产者对象
        producer.close();
    }
}
