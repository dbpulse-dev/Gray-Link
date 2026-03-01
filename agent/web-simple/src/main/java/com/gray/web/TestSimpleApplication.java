package com.gray.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication(scanBasePackages = {"com.gray"})
public class TestSimpleApplication {
    private static final Logger logger = LoggerFactory.getLogger(TestSimpleApplication.class);


    public static void main(String[] args) {
        SpringApplication.run(TestSimpleApplication.class, args);
        logger.info("Starting TestApplication successfully...");
    }

}
