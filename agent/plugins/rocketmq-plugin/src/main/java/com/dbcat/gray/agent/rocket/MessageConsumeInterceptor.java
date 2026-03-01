package com.dbcat.gray.agent.rocket;


import com.dbcat.gray.agent.core.context.Context;
import com.dbcat.gray.agent.core.context.InContextInterceptor;
import com.dbcat.gray.agent.core.dto.ComponentType;
import com.dbcat.gray.agent.core.dto.ConsumerMessage;
import com.dbcat.gray.agent.core.mq.MQListenerManager;
import com.dbcat.gray.agent.core.plugin.interceptor.enhance.EnhancedInstance;
import com.dbcat.gray.agent.core.plugin.interceptor.enhance.InstanceMethodsAroundInterceptor;
import com.dbcat.gray.agent.core.plugin.interceptor.enhance.MethodInterceptResult;
import org.apache.rocketmq.common.message.Message;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * @author Blackfost
 */
public class MessageConsumeInterceptor implements InContextInterceptor, InstanceMethodsAroundInterceptor {


    @Override
    public void beforeMethod(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes, MethodInterceptResult result) throws Throwable {
        if (!(allArguments[0] instanceof List)) {
            return;
        }
        //可能会同时收到灰度和正常消息,需要分组处理
        List messages = (List) allArguments[0];
        if (messages.isEmpty()) {
            return;
        }
        Context context = buildContext(messages);
        this.setInContext(context);
        messages.forEach(m -> MQListenerManager.onConsume(ConsumerMessage.build(ComponentType.ROCKET, m, context, allArguments)));
    }

    @Override
    public Object afterMethod(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes, Object ret) throws Throwable {
        this.clear();
        return ret;
    }

    private Context buildContext(List messages) {
        Message m = (Message) messages.get(0);
        Map<String, String> properties = m.getProperties();
        return new Context() {
            @Override
            public void put(String key, Object value) {
            }

            @Override
            public Object get(String key) {
                return properties.get(key);
            }
        };
    }


}
