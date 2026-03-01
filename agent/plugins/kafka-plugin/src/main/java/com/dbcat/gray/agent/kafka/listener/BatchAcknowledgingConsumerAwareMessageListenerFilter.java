package com.dbcat.gray.agent.kafka.listener;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.BatchAcknowledgingConsumerAwareMessageListener;
import org.springframework.kafka.listener.BatchMessageListener;
import org.springframework.kafka.support.Acknowledgment;

import java.util.List;

/**
 * Listener must be one of 'MessageListener', 'BatchMessageListener',
 *
 * @param <K>
 * @param <V>
 * @author Blackfost
 */
public class BatchAcknowledgingConsumerAwareMessageListenerFilter<K, V> implements BatchAcknowledgingConsumerAwareMessageListener<K, V> {


    private BatchMessageListener delegate;
    private boolean isGray;

    public BatchAcknowledgingConsumerAwareMessageListenerFilter(BatchMessageListener delegate, boolean isGray) {
        this.delegate = delegate;
        this.isGray = isGray;
    }

    @Override
    public void onMessage(List<ConsumerRecord<K, V>> data) {
        List<ConsumerRecord<K, V>> targetList = ListenerHelper.filter(data, isGray);
        delegate.onMessage(targetList);

    }


    @Override
    public void onMessage(List<ConsumerRecord<K, V>> data, Acknowledgment acknowledgment) {
        List<ConsumerRecord<K, V>> targetList = ListenerHelper.filter(data, isGray);
        delegate.onMessage(targetList, acknowledgment);
    }

    @Override
    public void onMessage(List<ConsumerRecord<K, V>> data, Consumer<?, ?> consumer) {
        List<ConsumerRecord<K, V>> targetList = ListenerHelper.filter(data, isGray);
        delegate.onMessage(targetList, consumer);
    }

    @Override
    public void onMessage(List<ConsumerRecord<K, V>> data, Acknowledgment acknowledgment, Consumer<?, ?> consumer) {
        List<ConsumerRecord<K, V>> targetList = ListenerHelper.filter(data, isGray);
        delegate.onMessage(targetList, acknowledgment, consumer);
    }

}
