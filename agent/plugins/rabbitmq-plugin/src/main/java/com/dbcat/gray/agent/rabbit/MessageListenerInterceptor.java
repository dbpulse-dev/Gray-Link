package com.dbcat.gray.agent.rabbit;


import com.dbcat.gray.agent.core.context.Context;
import com.dbcat.gray.agent.core.context.InContextInterceptor;
import com.dbcat.gray.agent.core.dto.ComponentType;
import com.dbcat.gray.agent.core.dto.ConsumerMessage;
import com.dbcat.gray.agent.core.mq.MQListenerManager;
import com.dbcat.gray.agent.core.plugin.interceptor.enhance.EnhancedInstance;
import com.dbcat.gray.agent.core.plugin.interceptor.enhance.InstanceMethodsAroundInterceptor;
import com.dbcat.gray.agent.core.plugin.interceptor.enhance.MethodInterceptResult;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 消费端,设置路由环境变量
 *
 * @author Blackfost
 */
public class MessageListenerInterceptor implements InContextInterceptor, InstanceMethodsAroundInterceptor {


    @Override
    public void beforeMethod(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes, MethodInterceptResult result) {
        if (allArguments[0] == null) {
            return;
        }
        List<Message> messages = toMessageList(allArguments);
        Context context = buildContext(messages);
        this.setInContext(context);
        MQListenerManager.onConsume(ConsumerMessage.build(ComponentType.RABBIT, messages, context, allArguments));
    }


    @Override
    public Object afterMethod(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes, Object ret) throws Throwable {
        clear();
        return ret;
    }


    private List<Message> toMessageList(Object[] allArguments) {
        if (allArguments[0] instanceof Message) {
            return Arrays.asList((Message) allArguments[0]);
        }
        return (List<Message>) allArguments[0];

    }

    private Context buildContext(List<Message> messages) {
        Message message = messages.get(0);
        MessageProperties properties = message.getMessageProperties();
        Map<String, Object> headers = properties.getHeaders();
        return new Context() {
            @Override
            public void put(String key, Object value) {
            }

            @Override
            public Object get(String key) {
                Object value = headers.get(key);
                if (value == null) {
                    return null;
                }
                return value.toString();
            }
        };
    }


}
