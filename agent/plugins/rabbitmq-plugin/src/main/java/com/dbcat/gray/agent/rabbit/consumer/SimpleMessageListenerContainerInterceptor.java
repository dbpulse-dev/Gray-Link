package com.dbcat.gray.agent.rabbit.consumer;


import com.dbcat.gray.agent.core.plugin.interceptor.enhance.EnhancedInstance;
import com.dbcat.gray.agent.core.plugin.interceptor.enhance.InstanceMethodsAroundInterceptor;
import com.dbcat.gray.agent.core.plugin.interceptor.enhance.MethodInterceptResult;
import com.dbcat.gray.agent.core.util.ReflectUtils;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;

import java.lang.reflect.Method;

/**
 * @author Blackfost
 */
public class SimpleMessageListenerContainerInterceptor implements InstanceMethodsAroundInterceptor {

    @Override
    public void beforeMethod(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes, MethodInterceptResult result) {
        SimpleMessageListenerContainer simpleMessageListenerContainer = (SimpleMessageListenerContainer) objInst;
        int concurrentConsumers = (int) ReflectUtils.getFieldValue(simpleMessageListenerContainer, "concurrentConsumers");
        //多增一下能通道(如果只配置一个通道)，用于双订阅
        ReflectUtils.setFieldValue(objInst, "concurrentConsumers", concurrentConsumers + 1);
    }

}
