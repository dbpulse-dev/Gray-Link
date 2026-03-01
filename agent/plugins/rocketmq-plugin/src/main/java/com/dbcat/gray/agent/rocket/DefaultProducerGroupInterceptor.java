package com.dbcat.gray.agent.rocket;


import com.dbcat.gray.agent.core.mq.MQConnectionManager;
import com.dbcat.gray.agent.core.plugin.interceptor.enhance.EnhancedInstance;
import com.dbcat.gray.agent.core.plugin.interceptor.enhance.InstanceConstructorInterceptor;
import com.dbcat.gray.agent.core.plugin.interceptor.enhance.InstanceMethodsAroundInterceptor;
import com.dbcat.gray.agent.core.plugin.interceptor.enhance.MethodInterceptResult;
import org.apache.rocketmq.client.producer.DefaultMQProducer;

import java.lang.reflect.Method;

/**
 * @author Blackfost
 */
public class DefaultProducerGroupInterceptor implements InstanceConstructorInterceptor, InstanceMethodsAroundInterceptor {

    @Override
    public void onConstruct(EnhancedInstance objInst, Object[] allArguments) throws Throwable {
        //可能会重入个构造，只需存一下
        if (objInst instanceof DefaultMQProducer) {
            DefaultMQProducer producer = (DefaultMQProducer) objInst;
            DefaultProducerConnection mqConnection = new DefaultProducerConnection("rocketMQ");
            mqConnection.setDelegate(producer);
            boolean add = MQConnectionManager.add(mqConnection);
            if (add) {
                processGroup(mqConnection);
                objInst.setGrayDynamicField(mqConnection);
            }
        }
    }

    @Override
    public void beforeMethod(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes, MethodInterceptResult result) throws Throwable {
        DefaultProducerConnection connection = (DefaultProducerConnection) objInst.getGrayDynamicField();
        processGroup(connection);
    }

    /**
     * 处理区别生产组
     *
     * @param connection
     */
    protected void processGroup(DefaultProducerConnection connection) {
        DefaultMQProducer delegate = connection.getDelegate();
        String producerGroup = delegate.getProducerGroup();
        if (producerGroup == null) {
            return;
        }
        connection.setInnerProducer(producerGroup.contains("_INNER_"));
    }

}
