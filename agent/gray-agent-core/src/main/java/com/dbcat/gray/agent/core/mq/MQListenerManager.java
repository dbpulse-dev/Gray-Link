package com.dbcat.gray.agent.core.mq;

import com.dbcat.gray.agent.core.dto.ComponentType;
import com.dbcat.gray.agent.core.dto.ConsumerMessage;
import com.dbcat.gray.agent.core.dto.PublishMessage;
import com.dbcat.gray.agent.core.logging.api.ILog;
import com.dbcat.gray.agent.core.logging.api.LogManager;
import com.dbcat.gray.agent.core.plugin.loader.AgentClassLoader;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

public class MQListenerManager {
    protected static final ILog logger = LogManager.getLogger(MQListenerManager.class);

    private static final Map<ComponentType, MQListener> listeners = new HashMap<>();

    static {
        for (final MQListener listener : ServiceLoader.load(MQListener.class, AgentClassLoader.getDefault())) {
            listeners.put(listener.type(), listener);
        }
    }

    public static void onPublish(PublishMessage message) {
        MQListener listener = listeners.get(message.getType());
        if (listener == null) {
            return;
        }
        try {
            listener.onPublish(message);
        } catch (Throwable t) {
            logger.warn("监听消息生产失败", t);
        }

    }

    public static void onConsume(ConsumerMessage message) {
        MQListener listener = listeners.get(message.getType());
        if (listener == null) {
            return;
        }
        try {
            listener.onConsume(message);
        } catch (Throwable t) {
            logger.warn("监听消费消息失败", t);
        }

    }
}
