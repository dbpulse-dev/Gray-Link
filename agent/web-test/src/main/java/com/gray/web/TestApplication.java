package com.gray.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication(scanBasePackages = {"com.gray"}, exclude = {DataSourceAutoConfiguration.class})
@EnableRabbit
@EnableKafka
public class TestApplication {
    private static final Logger logger = LoggerFactory.getLogger(TestApplication.class);


    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
        logger.info("Starting TestApplication successfully...");
    }

}
