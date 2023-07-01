package com.nowcoder.community;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class KafkaTest {

    @Autowired
    private Producer producer;

//    @Test
    public void test() throws InterruptedException {
        producer.pro("test", "你好");
        producer.pro("test", "123");
        Thread.sleep(1000 * 5);
    }

}

//这是spring-kafka，必须交给spring接管
@Component
class Producer {
    @Autowired
    private KafkaTemplate kafkaTemplate;

    void pro(String topic, String data) {
        kafkaTemplate.send(topic, data);
    }
}

@Component
class Consumer {

    @KafkaListener(topics = {"test"})
    public void handleMessage(ConsumerRecord record) {
        System.out.println(record.value());
    }
}