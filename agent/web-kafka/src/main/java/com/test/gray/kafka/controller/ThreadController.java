package com.test.gray.kafka.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.gray.kafka.dto.RestResult;
import com.test.gray.kafka.util.EnvUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/thread")
public class ThreadController {
    private static final Logger logger = LoggerFactory.getLogger(ThreadController.class);
    protected ThreadPoolExecutor executor = new ThreadPoolExecutor(20, 100, 30, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10000),
            runnable -> new Thread(null, runnable, "async-task", 0L), (runnable, threadPoolExecutor) -> logger.warn("健康检测线程池已满，丢弃任务..."));


    ObjectMapper objectMapper = new ObjectMapper();
    private AtomicInteger id = new AtomicInteger(1);

    @Autowired(required = false)
    private KafkaTemplate producer;

    @GetMapping("/runnable")
    public RestResult execute(int type) {
        String xEnv = EnvUtils.getXEnv();
        System.out.println("thread:" + Thread.currentThread().getName());
        logger.info("x_env:{}", xEnv);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                doSendMessage(type);
            }
        });
        return RestResult.buildSuccess();
    }

    @GetMapping("/runnable2")
    public RestResult execute2(int type) {
        //lambda Runnable 无法增强，使用thread-pool 拦截代理封装
        //需增强 jdk thread pool 才有效
        executor.execute(() -> {
            doSendMessage(type);
        });
        return RestResult.buildSuccess();
    }

    @GetMapping("/runnable4")
    public RestResult execute4(int type) {
        //lambda Runnable 无法增强，使用thread-pool 拦截代理封装
        new Thread() {
            @Override
            public void run() {
                doSendMessage(type);
            }
        }.start();
        return RestResult.buildSuccess();
    }

    @GetMapping("/callable")
    public RestResult submit(int type) {
        executor.submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                doSendMessage(type);
                return null;
            }
        });
        return RestResult.buildSuccess();
    }

    //ServerContextHolder.getData("x-env-")
    @GetMapping("/callable2")
    public RestResult submit2(int type) {
        //lambda Callable 无法增强，使用thread-pool 拦截代理封装
        //需增强 jdk thread pool 才有效
        executor.submit((Callable<Boolean>) () -> {
            doSendMessage(type);
            return null;
        });
        return RestResult.buildSuccess();
    }


    private void doSendMessage(int type) {
        String message = EnvUtils.getXEnv() == null ? "正常消息" : "灰度消息";
        if (type == 2) {
            logger.info("发送kakfa:{}", message);
            doSendKafkaMessage();
        }
    }


    private void doSendKafkaMessage() {
        String topic = "kafka-gray-test";
        try {
            producer.send(topic, "kafka " + EnvUtils.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
