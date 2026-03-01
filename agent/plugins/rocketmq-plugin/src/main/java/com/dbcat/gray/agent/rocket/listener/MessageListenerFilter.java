package com.dbcat.gray.agent.rocket.listener;

import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;

/**
 * 消息监听过滤(客户端方式过滤)
 * @author Blackfost
 */
public interface MessageListenerFilter {


    default List<MessageExt> filter(List<MessageExt> msgs) {
        if (getSubscriptType() == 0) {
            return ListenerHelper.filter(msgs, true);
        } else if (getSubscriptType() == 1) {
            return ListenerHelper.filter(msgs, false);
        }
        return msgs;
    }

    int getSubscriptType();
}
