package com.dbcat.gray.agent.rabbit;


import com.dbcat.gray.agent.core.plugin.interceptor.enhance.EnhancedInstance;
import com.dbcat.gray.agent.core.plugin.interceptor.enhance.InstanceMethodsAroundInterceptor;
import com.dbcat.gray.agent.core.plugin.interceptor.enhance.MethodInterceptResult;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;

import java.lang.reflect.Method;

/**
 * 获取发送消息连接连接
 *
 * @author Blackfost
 */
public class RabbitTemplateDoExecuteInterceptor implements InstanceMethodsAroundInterceptor {

    @Override
    public void beforeMethod(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes, MethodInterceptResult result) throws Throwable {
        ConnectionFactory connectionFactory = (ConnectionFactory) allArguments[1];
        if (!(connectionFactory instanceof EnhancedInstance)) {
            return;
        }
        //如果是灰度，返回的是灰度factory
        EnhancedInstance factoryEnhanceInstance = (EnhancedInstance) connectionFactory;
        RabbitMQConnection connection = (RabbitMQConnection) factoryEnhanceInstance.getGrayDynamicField();
        //获取发布factory
        allArguments[1] = connection.getPublishConnectionFactory();
    }
}
