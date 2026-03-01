package com.dbcat.gray.agent.kafka;


import com.dbcat.gray.agent.core.mq.MQConnectionManager;
import com.dbcat.gray.agent.core.plugin.interceptor.enhance.EnhancedInstance;
import com.dbcat.gray.agent.core.plugin.interceptor.enhance.InstanceConstructorInterceptor;
import com.dbcat.gray.agent.core.plugin.interceptor.enhance.InstanceMethodsAroundInterceptor;
import com.dbcat.gray.agent.core.plugin.interceptor.enhance.MethodInterceptResult;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

import java.lang.reflect.Method;

/**
 * @author Blackfost
 */
public class ConcurrentMessageListenerContainerInterceptor implements InstanceConstructorInterceptor, InstanceMethodsAroundInterceptor {

    @Override
    public void onConstruct(EnhancedInstance objInst, Object[] allArguments) throws Throwable {
        if (!(objInst instanceof ConcurrentMessageListenerContainer)) {
            return;
        }
        DefaultKafkaConsumerFactory consumerFactory = (DefaultKafkaConsumerFactory) allArguments[0];
        Object agentInCreate = consumerFactory.getConfigurationProperties().get("AGENT_IN_CREATE");
        if (agentInCreate != null) {
            return;
        }
        ConcurrentMessageListenerContainer listenerContainer = (ConcurrentMessageListenerContainer) objInst;
        KafkaConnection mqConnection = new KafkaConnection("kafka", consumerFactory);
        mqConnection.setDelegate(listenerContainer);
        boolean add = MQConnectionManager.add(mqConnection);
        if (add) {
            objInst.setGrayDynamicField(mqConnection);
        }

    }


    @Override
    public void beforeMethod(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes, MethodInterceptResult result) throws Throwable {
        //onStart 构建订阅
        KafkaConnection connection = (KafkaConnection) objInst.getGrayDynamicField();
        if (connection == null) {
            //agent内部创建的consumer
            return;
        }
        //不调用start方法,由内容的实现调用
        result.defineReturnValue(null);
        connection.buildConsumers();
        connection.start();
    }
}
