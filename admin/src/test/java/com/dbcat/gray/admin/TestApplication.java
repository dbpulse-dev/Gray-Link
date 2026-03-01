package com.dbcat.gray.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.dbcat", "test.com.dbcat.official"})
@MapperScan(basePackages = {"com.dbcat.**.mapper"})
public class TestApplication {

    private static final Logger logger = LoggerFactory.getLogger(TestApplication.class);


    public static void main(String[] args) {

        SpringApplication.run(TestApplication.class, args);
        logger.info("Starting TestApplication successfully...");
    }


}
