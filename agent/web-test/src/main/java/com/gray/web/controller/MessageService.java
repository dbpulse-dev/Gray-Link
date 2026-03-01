package com.gray.web.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gray.web.dto.User;
import com.gray.web.util.EnvUtils;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class MessageService {

    private static final Logger logger = LoggerFactory.getLogger(MessageService.class);
    @Resource
    private RabbitTemplate rabbitTemplate;

    @Autowired(required = false)
    private DefaultMQProducer defaultProducer;

    ObjectMapper objectMapper = new ObjectMapper();
    private AtomicInteger id = new AtomicInteger(1);

    @Autowired(required = false)
    private KafkaTemplate producer;


    @Async
    public void asyncSend(int type) {
        doSendMessage(type);
    }


    public void doSendMessage(int type) {
        String message = EnvUtils.getXEnv() == null ? "正常消息" : "灰度消息";
        if (type == 0) {
            logger.info("发送rabbit:{}", message);
            doSendRabbitMessage();
        }
        if (type == 1) {
            logger.info("发送rocket:{}", message);
            doSendRocketMessage();
        }
        if (type == 2) {
            logger.info("发送kakfa:{}", message);
            doSendKafkaMessage();
        }
    }


    public void doSendRabbitMessage() {
        User user = new User();
        user.setAge(id.incrementAndGet());
        user.setName("rabbit" + EnvUtils.getMessage(""));
        try {
            MessageProperties properties = new MessageProperties();
            org.springframework.amqp.core.Message message = new org.springframework.amqp.core.Message(objectMapper.writeValueAsString(user).getBytes(), properties);
            //rabbitTemplate.convertAndSend("xie_test", "xie-rout-key", objectMapper.writeValueAsString(user));
            rabbitTemplate.send("xie_test", "xie-rout-key", message);
        } catch (Exception e) {
            System.out.println("mq异常了:" + e.toString());
        }
    }

    public void doSendRocketMessage() {
        try {
            defaultProducer.send(buildRocketMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Message buildRocketMessage() throws JsonProcessingException {
        User user = new User();
        user.setAge(id.incrementAndGet());
        user.setName("rocket" + EnvUtils.getMessage(""));
        String data = objectMapper.writeValueAsString(user);
        String msgId = UUID.randomUUID().toString();
        Message msg = new Message("test-topic-a",
                null,
                msgId,
                data.getBytes());
        return msg;
    }

    public void doSendKafkaMessage() {
        String topic = "kafka-gray-test";
        try {
            producer.send(topic, "kafka " + EnvUtils.getMessage(""));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
