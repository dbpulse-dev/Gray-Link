package com.dbcat.gray.server;

import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ServiceController {
    protected static final Logger logger = LoggerFactory.getLogger(ServiceController.class);

    @DubboReference
    private HelloService helloService;

    @GetMapping("/hello")
    public String hello() {
        logger.info("调用远程服务hello");
        return helloService.sayHello("a");
    }
}