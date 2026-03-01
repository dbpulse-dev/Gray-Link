package com.test.gray.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication(scanBasePackages = {"com.test.gray"}, exclude = {DataSourceAutoConfiguration.class})
@EnableKafka
public class KafkaApplication {
    private static final Logger logger = LoggerFactory.getLogger(KafkaApplication.class);


    public static void main(String[] args) {
        SpringApplication.run(KafkaApplication.class, args);
        logger.info("Starting TestApplication successfully...");
    }

}
