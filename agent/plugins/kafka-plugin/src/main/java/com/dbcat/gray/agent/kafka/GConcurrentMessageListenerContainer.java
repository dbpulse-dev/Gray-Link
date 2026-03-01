package com.dbcat.gray.agent.kafka;

import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;

/**
 * @author Blackfost
 */
public class GConcurrentMessageListenerContainer extends ConcurrentMessageListenerContainer {
    public GConcurrentMessageListenerContainer(ConsumerFactory consumerFactory, ContainerProperties containerProperties) {
        super(consumerFactory, containerProperties);
    }

    @Override
    public void doStart() {
        super.doStart();
    }
}
