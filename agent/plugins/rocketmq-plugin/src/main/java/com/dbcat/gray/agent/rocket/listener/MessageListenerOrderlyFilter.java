package com.dbcat.gray.agent.rocket.listener;

import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListener;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;

/**
 * @author Blackfost
 */
public class MessageListenerOrderlyFilter implements MessageListenerFilter, MessageListenerOrderly {

    private MessageListenerOrderly listener;

    /**
     * 0:仅订阅灰度消息，1仅订阅正常消息，2同时订阅正常和灰度消息
     */
    private int subscriptType;

    private MessageListenerOrderlyFilter() {

    }

    public static MessageListener build(MessageListenerOrderly listener, int subscriptType) {
        MessageListenerOrderlyFilter delegate = new MessageListenerOrderlyFilter();
        delegate.subscriptType = subscriptType;
        delegate.listener = listener;
        return delegate;
    }


    @Override
    public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {
        List<MessageExt> targets = filter(msgs);
        if (targets.isEmpty()) {
            return ConsumeOrderlyStatus.SUCCESS;
        }
        return listener.consumeMessage(targets, context);
    }


    @Override
    public int getSubscriptType() {
        return subscriptType;
    }
}
