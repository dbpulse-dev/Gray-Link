package com.dbcat.gray.agent.kafka.listener;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.AcknowledgingConsumerAwareMessageListener;
import org.springframework.kafka.listener.GenericMessageListener;
import org.springframework.kafka.support.Acknowledgment;

/**
 * Listener must be one of 'MessageListener', 'BatchMessageListener',
 *
 * @param <K>
 * @param <V>
 * @author Blackfost
 */
public class AcknowledgingConsumerAwareMessageListenerFilter<K, V> implements AcknowledgingConsumerAwareMessageListener<K, V> {


    private GenericMessageListener delegate;
    private boolean isGray;

    public AcknowledgingConsumerAwareMessageListenerFilter(GenericMessageListener delegate, boolean isGray) {
        this.delegate = delegate;
        this.isGray = isGray;
    }


    @Override
    public void onMessage(ConsumerRecord<K, V> data, Acknowledgment acknowledgment) {
        if (ListenerHelper.filter(data, isGray)) {
            delegate.onMessage(data, acknowledgment);
        }
    }

    @Override
    public void onMessage(ConsumerRecord<K, V> data, Consumer<?, ?> consumer) {
        if (ListenerHelper.filter(data, isGray)) {
            delegate.onMessage(data, consumer);
        }
    }

    @Override
    public void onMessage(ConsumerRecord<K, V> data, Acknowledgment acknowledgment, Consumer<?, ?> consumer) {
        if (ListenerHelper.filter(data, isGray)) {
            delegate.onMessage(data, acknowledgment, consumer);
        }
    }

}
