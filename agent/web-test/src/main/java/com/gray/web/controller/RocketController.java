package com.gray.web.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gray.web.dto.User;
import com.gray.web.util.EnvUtils;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.apache.rocketmq.spring.support.DefaultRocketMQListenerContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


@RestController
@RequestMapping("/rocket")
public class RocketController {

    protected static final Logger logger = LoggerFactory.getLogger(RocketController.class);

    protected ThreadPoolExecutor executor = new ThreadPoolExecutor(20, 100, 30, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10000),
            runnable -> new Thread(null, runnable, "async-task", 0L), (runnable, threadPoolExecutor) -> logger.warn("健康检测线程池已满，丢弃任务..."));


    @Autowired(required = false)
    private DefaultMQProducer defaultProducer;

    @Autowired(required = false)
    private DefaultRocketMQListenerContainer rocketMQListenerContainer;

    @Autowired
    private ApplicationContext applicationContext;
    private AtomicInteger id = new AtomicInteger(1);

    ObjectMapper objectMapper = new ObjectMapper();


    @GetMapping("/send")
    public String send(String topic, String tags, HttpServletRequest request) throws InterruptedException, RemotingException, MQClientException, MQBrokerException, JsonProcessingException {
        logger.info("发送:{}", EnvUtils.getMessage(topic));
        defaultProducer.send(buildMessage(topic, tags));
        return "success";
    }

    @GetMapping("/send2")
    public String send2(String topic, String tags) throws InterruptedException, RemotingException, MQClientException, MQBrokerException, JsonProcessingException {
        logger.info("发送:{}", EnvUtils.getMessage(topic));
        defaultProducer.send(Arrays.asList(buildMessage(topic, tags), buildMessage(topic, tags)));
        return "success";
    }


    @GetMapping("/send3")
    public String send3(String topic, String tags) throws InterruptedException, RemotingException, MQClientException, MQBrokerException, JsonProcessingException {
        logger.info("同时发送正常和灰度消息");
        EnvUtils.removeEnv();
        executor.execute(() -> {
            EnvUtils.setEnv();
            logger.info("发送:{}", EnvUtils.getMessage(topic));
            try {
                defaultProducer.send(buildMessage(topic, tags));
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                EnvUtils.removeEnv();
            }
        });
        executor.execute(() -> {
            logger.info("发送:{}", EnvUtils.getMessage(topic));
            try {
                defaultProducer.send(buildMessage(topic, tags));
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                EnvUtils.removeEnv();
            }
        });
        return "success";
    }


    private Message buildMessage(String topic, String tags) throws JsonProcessingException {
        User user = new User();
        user.setAge(id.incrementAndGet());
        user.setName(EnvUtils.getMessage(topic));
        String data = objectMapper.writeValueAsString(user);
        String msgId = UUID.randomUUID().toString();
        Message msg = new Message(topic,
                null,
                msgId,
                data.getBytes());
        if (tags != null) {
            msg.setTags(tags);
        }
        return msg;
    }


}
