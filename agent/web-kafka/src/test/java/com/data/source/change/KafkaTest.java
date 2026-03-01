package com.data.source.change;


import com.test.gray.kafka.KafkaApplication;
import com.test.gray.kafka.util.EnvUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = KafkaApplication.class)
public class KafkaTest {

    @Autowired
    KafkaProperties properties;

    @Autowired(required = false)
    private KafkaTemplate producer;

    @Test
    public void send() throws InterruptedException {
        String topic = "kafka-gray-test";
        EnvUtils.setEnv();
        while (true) {
            System.out.println("发送消息");
            producer.send(topic, "abc");
            Thread.sleep(200L);
        }

        //kafkaTemplate.send("daojia-thirdparty-store-product-log-dev","abc");

    }
}
