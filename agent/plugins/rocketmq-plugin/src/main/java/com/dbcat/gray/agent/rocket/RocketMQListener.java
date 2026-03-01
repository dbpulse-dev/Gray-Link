package com.dbcat.gray.agent.rocket;

import com.dbcat.gray.agent.core.dto.ComponentType;
import com.dbcat.gray.agent.core.dto.ConsumerMessage;
import com.dbcat.gray.agent.core.dto.PublishMessage;
import com.dbcat.gray.agent.core.meter.CounterManager;
import com.dbcat.gray.agent.core.mq.MQListener;
import org.apache.rocketmq.common.message.Message;

/**
 * @author Blackfost
 */
public class RocketMQListener implements MQListener<Message, Message> {

    @Override
    public ComponentType type() {
        return ComponentType.ROCKET;
    }

    @Override
    public void onPublish(PublishMessage<Message> message) {
        CounterManager.increasePublish(type(), message.getRoutingEnv());
    }

    @Override
    public void onConsume(ConsumerMessage<Message> message) {
        CounterManager.increaseConsume(type(), message.getRoutingEnv());
    }
}
