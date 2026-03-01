package com.dbcat.gray.agent.rabbit;


import com.dbcat.gray.agent.core.context.Context;
import com.dbcat.gray.agent.core.context.OutContextInterceptor;
import com.dbcat.gray.agent.core.dto.ComponentType;
import com.dbcat.gray.agent.core.dto.PublishMessage;
import com.dbcat.gray.agent.core.mq.MQListenerManager;
import com.dbcat.gray.agent.core.plugin.interceptor.enhance.EnhancedInstance;
import com.dbcat.gray.agent.core.plugin.interceptor.enhance.InstanceMethodsAroundInterceptor;
import com.dbcat.gray.agent.core.plugin.interceptor.enhance.MethodInterceptResult;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;

import java.lang.reflect.Method;

/**
 * 标记生产消息灰度变量
 *
 * @author Blackfost
 */
public class MessagePublishInterceptor implements OutContextInterceptor, InstanceMethodsAroundInterceptor {

    @Override
    public void beforeMethod(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes, MethodInterceptResult result) {
        Message message = (Message) allArguments[2];
        Context context = buildContext(message);
        setOutContext(context);
        MQListenerManager.onPublish(PublishMessage.build(ComponentType.RABBIT, message, context, allArguments));
    }

    private Context buildContext(Message message) {
        MessageProperties messageProperties = message.getMessageProperties();
        return new Context() {
            @Override
            public void put(String key, Object value) {
                messageProperties.setHeader(key, value);
            }

            @Override
            public Object get(String key) {
                return messageProperties.getHeader(key);
            }
        };
    }
}
