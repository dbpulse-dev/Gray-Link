package com.dbcat.gray.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;


@RestController
@RequestMapping("/api")
public class ServiceController {
    protected static final Logger logger = LoggerFactory.getLogger(ServiceController.class);

    @GetMapping("/hello")
    public String hello(HttpServletRequest request) {
        String header = request.getHeader("x_env");
        logger.info("服务接收到的请求头x-env:{}", header);
        logger.info("server 接收到请求");
        return "Hello from Server B! x-env:" + header;
    }
}