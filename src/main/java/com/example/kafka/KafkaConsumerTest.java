package com.example.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class KafkaConsumerTest {
    public static void main(String[] args) {

        Map<String, Object> consumerConfig = new HashMap<>();
        consumerConfig.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        consumerConfig.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerConfig.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        consumerConfig.put(ConsumerConfig.GROUP_ID_CONFIG, "luyian");

        // 创建消费者对象
        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(consumerConfig);
        // 订阅主题
        consumer.subscribe(Collections.singletonList("test"));

        // 从 kafka 的主题中获取数据
        while (true) {
            ConsumerRecords<String, String> datas = consumer.poll(100);
            for (ConsumerRecord<String, String> data : datas) {
                System.out.println(data);
            }
        }

    }
}
