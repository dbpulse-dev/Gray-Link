package com.dbcat.gray.agent.rabbit.consumer;


import com.dbcat.gray.agent.core.plugin.interceptor.enhance.EnhancedInstance;
import com.dbcat.gray.agent.core.plugin.interceptor.enhance.InstanceConstructorInterceptor;
import com.dbcat.gray.agent.core.plugin.interceptor.enhance.InstanceMethodsAroundInterceptor;
import com.dbcat.gray.agent.core.plugin.interceptor.enhance.MethodInterceptResult;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.listener.BlockingQueueConsumer;

import java.lang.reflect.Method;

import static com.dbcat.gray.agent.rabbit.consumer.BlockingQueueManager.bindSubscript;

/**
 * 处理订阅 virtualhost
 *
 * @author Blackfost
 */
public class BlockingQueueConsumeInterceptor implements InstanceConstructorInterceptor, InstanceMethodsAroundInterceptor {

    @Override
    public void onConstruct(EnhancedInstance objInst, Object[] allArguments) {
        if (!(allArguments[0] instanceof CachingConnectionFactory)) {
            return;
        }
        CachingConnectionFactory connectionFactory = (CachingConnectionFactory) allArguments[0];
        BlockingQueueConsumer consumer = (BlockingQueueConsumer) objInst;
        String[] queues = (String[]) allArguments[allArguments.length - 1];
        BlockingQueueConsumerHolder consumerHolder = bindSubscript(consumer, queues, connectionFactory);
        objInst.setGrayDynamicField(consumerHolder);
        BlockingQueueManager.add(consumerHolder);
    }

    @Override
    public void beforeMethod(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes, MethodInterceptResult result) {
        BlockingQueueManager.remove((BlockingQueueConsumer) objInst);
    }

}
