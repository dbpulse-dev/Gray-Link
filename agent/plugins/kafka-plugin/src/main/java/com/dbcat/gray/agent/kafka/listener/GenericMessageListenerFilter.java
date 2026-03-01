package com.dbcat.gray.agent.kafka.listener;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.GenericMessageListener;
import org.springframework.kafka.support.Acknowledgment;

/**
 * @author Blackfost
 */
public class GenericMessageListenerFilter implements GenericMessageListener<ConsumerRecord> {


    private GenericMessageListener delegate;
    private boolean isGray;

    public GenericMessageListenerFilter(GenericMessageListener delegate, boolean isGray) {
        this.delegate = delegate;
        this.isGray = isGray;
    }


    @Override
    public void onMessage(ConsumerRecord consumerRecord) {

    }

    @Override
    public void onMessage(ConsumerRecord data, Acknowledgment acknowledgment) {
        if (ListenerHelper.filter(data, isGray)) {
            delegate.onMessage(data, acknowledgment);
        }
    }

    @Override
    public void onMessage(ConsumerRecord data, Consumer<?, ?> consumer) {
        if (ListenerHelper.filter(data, isGray)) {
            delegate.onMessage(data, consumer);
        }
    }

    @Override
    public void onMessage(ConsumerRecord data, Acknowledgment acknowledgment, Consumer<?, ?> consumer) {
        if (ListenerHelper.filter(data, isGray)) {
            delegate.onMessage(data, acknowledgment, consumer);
        }
    }

}
