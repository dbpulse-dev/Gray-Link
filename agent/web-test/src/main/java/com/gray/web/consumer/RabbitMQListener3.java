package com.gray.web.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gray.web.util.EnvUtils;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;

import java.io.IOException;

/**
 * 监听rabbit mq 消息
 * <p>
 *
 * @version 1.0.0
 * @date 2019/10/13
 */
//@Component
public class RabbitMQListener3 {
    protected static final Logger logger = LoggerFactory.getLogger(RabbitMQListener3.class);
    ObjectMapper objectMapper = new ObjectMapper();

    @RabbitListener(queues = "gray-test-queue", concurrency = "2")
    @RabbitHandler
    public void listenerShoppeMq(Message message2, String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        System.out.println("收到消息:" + objectMapper.writeValueAsString(message));
        logger.info("收到rabbit:{}", EnvUtils.getMessage(""));
    }

}
