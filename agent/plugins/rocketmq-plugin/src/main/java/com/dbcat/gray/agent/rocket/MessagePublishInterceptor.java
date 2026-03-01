package com.dbcat.gray.agent.rocket;


import com.dbcat.gray.agent.core.context.Context;
import com.dbcat.gray.agent.core.context.OutContextInterceptor;
import com.dbcat.gray.agent.core.dto.ComponentType;
import com.dbcat.gray.agent.core.dto.PublishMessage;
import com.dbcat.gray.agent.core.mq.MQListenerManager;
import com.dbcat.gray.agent.core.plugin.interceptor.enhance.EnhancedInstance;
import com.dbcat.gray.agent.core.plugin.interceptor.enhance.InstanceMethodsAroundInterceptor;
import com.dbcat.gray.agent.core.plugin.interceptor.enhance.MethodInterceptResult;
import org.apache.rocketmq.common.message.Message;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author Blackfost
 */
public class MessagePublishInterceptor implements OutContextInterceptor, InstanceMethodsAroundInterceptor {


    @Override
    public void beforeMethod(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes, MethodInterceptResult result) {
        DefaultProducerConnection connection = (DefaultProducerConnection) objInst.getGrayDynamicField();
        if (connection.isInnerProducer()) {
            return;
        }
        List<Message> messages = toListMessage(allArguments);
        RocketContext context = new RocketContext(messages);
        setOutContext(context);
        messages.forEach(m -> MQListenerManager.onPublish(PublishMessage.build(ComponentType.ROCKET, m, context, allArguments)));
    }


    private static class RocketContext implements Context {
        List<Message> messages;

        RocketContext(List<Message> messages) {
            this.messages = messages;
        }

        @Override
        public void put(String key, Object value) {
            for (Message message : messages) {
                message.putUserProperty(key, value.toString());
            }
        }

        @Override
        public Object get(String key) {
            Message message = messages.get(0);
            return message.getProperties().get(key);
        }
    }

    protected List<Message> toListMessage(Object[] allArguments) {
        Object msg = allArguments[0];
        if (msg instanceof Message) {
            Message message = (Message) msg;
            List<Message> messages = new ArrayList<>(1);
            messages.add(message);
            return messages;
        }
        if (!(msg instanceof Collection)) {
            return Collections.emptyList();
        }
        Collection collection = (Collection) msg;
        List<Message> messages = new ArrayList<>(collection.size());
        for (Object m : collection) {
            Message message = (Message) m;
            messages.add(message);
        }
        return messages;
    }
}
