package com.dbcat.gray.agent.rocket.listener;

import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListener;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;

/**
 * @author Blackfost
 */
public class MessageListenerConcurrentlyFilter implements MessageListenerFilter, MessageListenerConcurrently {

    private MessageListenerConcurrently listener;

    private int subscriptType;

    private MessageListenerConcurrentlyFilter() {

    }

    public static MessageListener build(MessageListenerConcurrently listener, int subscriptType) {
        MessageListenerConcurrentlyFilter delegate = new MessageListenerConcurrentlyFilter();
        delegate.subscriptType = subscriptType;
        delegate.listener = listener;
        return delegate;
    }

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
        List<MessageExt> targets = filter(msgs);
        if (targets.isEmpty()) {
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }
        return listener.consumeMessage(targets, context);
    }


    @Override
    public int getSubscriptType() {
        return subscriptType;
    }
}
