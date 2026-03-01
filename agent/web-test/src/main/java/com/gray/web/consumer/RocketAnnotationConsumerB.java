package com.gray.web.consumer;

import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


//@Component
@RocketMQMessageListener(topic = "test-topic-b", consumerGroup = "test-app")
public class RocketAnnotationConsumerB implements RocketMQListener<MessageExt> {
    protected static final Logger logger = LoggerFactory.getLogger(RocketAnnotationConsumerB.class);

    @Override
    public void onMessage(MessageExt messageExt) {
        logger.info("收到{}", new String(messageExt.getBody()));
    }
}

