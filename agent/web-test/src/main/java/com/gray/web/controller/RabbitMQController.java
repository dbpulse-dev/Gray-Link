package com.gray.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gray.web.dto.User;
import com.gray.web.util.EnvUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


@RequestMapping("/rabbit")
@RestController
public class RabbitMQController implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQController.class);

    protected ThreadPoolExecutor executor = new ThreadPoolExecutor(20, 100, 30, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10000),
            runnable -> new Thread(null, runnable, "async-task", 0L), (runnable, threadPoolExecutor) -> logger.warn("健康检测线程池已满，丢弃任务..."));

    ObjectMapper objectMapper = new ObjectMapper();
    @Resource
    private RabbitTemplate rabbitTemplate;

    //    @Resource(name = "rabbitTemplat2")
    private RabbitTemplate rabbitTemplat2;


    private ApplicationContext applicationContext;


    @GetMapping("/send")
    public String sendMessage() throws InterruptedException {
        logger.info("发送:{}", EnvUtils.getMessage(""));
        doSendMessage();
        return "success";
    }

    @GetMapping("/send2")
    public String sendMessage2() throws InterruptedException {
        System.out.println("异步发送消息...");
        executor.execute(() -> doSendMessage());
        return "success";
    }

    @GetMapping("/send3")
    public String sendMessage3() throws InterruptedException {
        logger.info("发送:{}", EnvUtils.getMessage(""));
        User user = new User();
        user.setAge(129);
        AtomicInteger traceId = new AtomicInteger(1);
        user.setName(EnvUtils.getMessage(""));
        try {
            traceId.getAndIncrement();
            rabbitTemplate.convertAndSend("gray-test-exchange", "gray-test-route", objectMapper.writeValueAsString(user));
        } catch (Exception e) {
            System.out.println("mq异常了:" + e.toString());
        }
        return "success";
    }

    @Autowired
    private CachingConnectionFactory connectionFactory;

    @GetMapping("/create/connection")
    public String createConnection() throws InterruptedException {
        Connection connection = connectionFactory.createConnection();
        return "success:" + connection.toString();
    }
    AtomicInteger traceId = new AtomicInteger(1);

    private void doSendMessage() {
        User user = new User();
        user.setAge(129);
        try {
            user.setName(EnvUtils.getMessage("") + traceId.getAndIncrement());
            rabbitTemplate.convertAndSend("gray-test-exchange", "gray-test-rout-key", objectMapper.writeValueAsString(user));
        } catch (Exception e) {
            System.out.println("mq异常了:" + e.toString());
        }
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        //doSendMessage();
    }
}
