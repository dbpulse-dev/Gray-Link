package com.gray.web.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.gray.web.util.EnvUtils;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


@RestController
@RequestMapping("/kafka")
public class KafkaController {

    protected static final Logger logger = LoggerFactory.getLogger(KafkaController.class);

    protected ThreadPoolExecutor executor = new ThreadPoolExecutor(20, 100, 30, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10000),
            runnable -> new Thread(null, runnable, "async-task", 0L), (runnable, threadPoolExecutor) -> logger.warn("健康检测线程池已满，丢弃任务..."));

    @Autowired(required = false)
    private KafkaTemplate producer;

    private AtomicInteger id = new AtomicInteger(1);



    @GetMapping("/send")
    public String send(String topic) throws InterruptedException, RemotingException, MQClientException, MQBrokerException, JsonProcessingException {
        topic = "kafka-gray-test";
        logger.info("发送:{}", EnvUtils.getMessage(topic));
        doSendKafkaMessage();
        return "success";
    }

    private void doSendKafkaMessage() {
        String topic = "kafka-gray-test";
        try {
            producer.send(topic, "kafka " + EnvUtils.getMessage(topic)+id.getAndIncrement());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
