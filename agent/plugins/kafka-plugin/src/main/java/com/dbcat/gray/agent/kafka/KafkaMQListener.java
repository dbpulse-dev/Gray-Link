package com.dbcat.gray.agent.kafka;

import com.dbcat.gray.agent.core.dto.ComponentType;
import com.dbcat.gray.agent.core.dto.ConsumerMessage;
import com.dbcat.gray.agent.core.dto.PublishMessage;
import com.dbcat.gray.agent.core.meter.CounterManager;
import com.dbcat.gray.agent.core.mq.MQListener;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;


/**
 * @author Blackfost
 */
public class KafkaMQListener implements MQListener<ProducerRecord, ConsumerRecord> {

    @Override
    public ComponentType type() {
        return ComponentType.KAFKA;
    }

    @Override
    public void onPublish(PublishMessage<ProducerRecord> message) {
        CounterManager.increasePublish(type(), message.getRoutingEnv());
    }

    @Override
    public void onConsume(ConsumerMessage<ConsumerRecord> message) {
        CounterManager.increaseConsume(type(), message.getRoutingEnv());
    }
}
