package com.dbcat.gray.agent.rocket.listener;

import com.dbcat.gray.agent.core.AppInstance;
import com.dbcat.gray.agent.core.logging.api.ILog;
import com.dbcat.gray.agent.core.logging.api.LogManager;
import org.apache.rocketmq.client.consumer.listener.MessageListener;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;
import java.util.stream.Collectors;

import static com.dbcat.gray.agent.core.conf.Config.Agent.ROUTE_LABEL;

/**
 * @author Blackfost
 */
public class ListenerHelper {

    private static final ILog logger = LogManager.getLogger(ListenerHelper.class);

    public static List<MessageExt> filter(List<MessageExt> msgs, boolean isGray) {
        if(AppInstance.isDoubleSubscript()){
            //双订阅不过消息
            return msgs;
        }
        if (isGray) {
            return msgs.stream().filter(m -> {
                String routeEnv = m.getProperty(ROUTE_LABEL);
                return routeEnv != null;
            }).collect(Collectors.toList());
        } else {
            return msgs.stream().filter(m -> {
                String routeEnv = m.getProperty(ROUTE_LABEL);
                return routeEnv == null;
            }).collect(Collectors.toList());
        }
    }


    public static MessageListener buildListenerFilter(MessageListener listener, int subscriptType) {
        if (listener instanceof MessageListenerConcurrently) {
            return MessageListenerConcurrentlyFilter.build((MessageListenerConcurrently) listener, subscriptType);
        }
        if (listener instanceof MessageListenerOrderly) {
            return MessageListenerOrderlyFilter.build((MessageListenerOrderly) listener, subscriptType);
        }
        logger.warn("rocket mq 没有匹配到 相应的listener 代理");
        return listener;
    }

}
