package com.gray.web.consumer;

import com.gray.web.util.EnvUtils;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component
@RocketMQMessageListener(topic = "test-topic-a", consumerGroup = "test-app")
public class RocketAnnotationConsumerA implements RocketMQListener<MessageExt> {
    protected static final Logger logger = LoggerFactory.getLogger(RocketAnnotationConsumerA.class);

    @Override
    public void onMessage(MessageExt messageExt) {
        logger.info("收到rocket 消息,x_env:{},内容{}", EnvUtils.getXEnv(), new String(messageExt.getBody()));
    }
}

