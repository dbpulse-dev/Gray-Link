package com.dbcat.gray.agent.rabbit;

import com.dbcat.gray.agent.core.dto.ComponentType;
import com.dbcat.gray.agent.core.dto.ConsumerMessage;
import com.dbcat.gray.agent.core.dto.PublishMessage;
import com.dbcat.gray.agent.core.meter.CounterManager;
import com.dbcat.gray.agent.core.mq.MQListener;
import org.springframework.amqp.core.Message;

import java.util.List;

/**
 * @author Blackfost
 */
public class RabbitMQListener implements MQListener<Message, List<Message>> {

    @Override
    public ComponentType type() {
        return ComponentType.RABBIT;
    }

    @Override
    public void onPublish(PublishMessage<Message> message) {
        CounterManager.increasePublish(type(), message.getRoutingEnv());
    }

    @Override
    public void onConsume(ConsumerMessage<List<Message>> message) {
        CounterManager.increaseConsume(type(), message.getRoutingEnv());
    }
}
