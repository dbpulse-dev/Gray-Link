package com.dbcat.gray.admin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(scanBasePackages = {"com.dbcat"})
public class GrayAdminApplication {

    private static final Logger logger = LoggerFactory.getLogger(GrayAdminApplication.class);


    public static void main(String[] args) {
        SpringApplication.run(GrayAdminApplication.class, args);
        logger.info("Starting AdminApplication successfully...");
    }

}
