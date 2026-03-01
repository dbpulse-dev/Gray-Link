package com.gray.web.consumer;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import java.util.List;


//@Component
public class RocketmqClientConsumer2 {

    protected static final Logger logger = LoggerFactory.getLogger(RocketmqClientConsumer2.class);

    @Value("${spring.application.name}")
    private String appName;

    @Value("${rocketmq.name-server}")
    private String nameServers;

    @Bean
    public DefaultMQPushConsumer rocketClientConsumer() {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer();
        try {

            consumer.setConsumerGroup(appName);
            consumer.setNamesrvAddr(nameServers);
            String[] topics = new String[]{"test-topic-a", "test-topic-b"};
            for (String topic : topics) {
                consumer.subscribe(topic, "test");
            }
            consumer.registerMessageListener(new MessageListenerConcurrently() {

                @Override
                public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                    for (MessageExt messageExt : msgs) {
                        logger.info("消费消息:{},标签：{}", new String(messageExt.getBody()), messageExt.getTags());
                    }
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }
            });
            consumer.setMaxReconsumeTimes(10);
            consumer.start();
        } catch (Exception e) {
            logger.error("注册topic监听异常", e);
        }
        return consumer;
    }


}
