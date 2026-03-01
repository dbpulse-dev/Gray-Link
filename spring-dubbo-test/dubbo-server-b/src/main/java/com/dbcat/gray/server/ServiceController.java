package com.dbcat.gray.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ServiceController {
    protected static final Logger logger = LoggerFactory.getLogger(ServiceController.class);

    @GetMapping("/hello")
    public String hello() {
        logger.info("server 接收到请求");
        return "Hello from Server B!";
    }
}