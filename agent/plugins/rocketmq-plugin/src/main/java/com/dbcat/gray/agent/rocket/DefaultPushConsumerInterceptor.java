package com.dbcat.gray.agent.rocket;


import com.dbcat.gray.agent.core.logging.api.ILog;
import com.dbcat.gray.agent.core.logging.api.LogManager;
import com.dbcat.gray.agent.core.mq.MQConnectionManager;
import com.dbcat.gray.agent.core.plugin.interceptor.enhance.EnhancedInstance;
import com.dbcat.gray.agent.core.plugin.interceptor.enhance.InstanceConstructorInterceptor;
import com.dbcat.gray.agent.core.plugin.interceptor.enhance.InstanceMethodsAroundInterceptor;
import com.dbcat.gray.agent.core.plugin.interceptor.enhance.MethodInterceptResult;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;

import java.lang.reflect.Method;

/**
 * @author Blackfost
 */
public class DefaultPushConsumerInterceptor implements InstanceConstructorInterceptor, InstanceMethodsAroundInterceptor {

    private static final ILog logger = LogManager.getLogger(DefaultPushConsumerInterceptor.class);


    @Override
    public void onConstruct(EnhancedInstance objInst, Object[] allArguments) {
        DefaultMQPushConsumer consumer = (DefaultMQPushConsumer) objInst;
        if ("AGENT_IN_CREATE".equals(consumer.getConsumerGroup())) {
            return;
        }
        DefaultConsumerConnection mqConnection = new DefaultConsumerConnection("rocketMQ");
        mqConnection.setDelegate(consumer);
        // 可能会重入个构造，只需存一下
        boolean add = MQConnectionManager.add(mqConnection);
        if (add) {
            objInst.setGrayDynamicField(mqConnection);
        }
    }

    @Override
    public void beforeMethod(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes, MethodInterceptResult result) throws Throwable {
        DefaultConsumerConnection connection = (DefaultConsumerConnection) objInst.getGrayDynamicField();
        if (connection == null) {
            //agent内部创建的consumer
            return;
        }
        if (method.getName().contains("subscribe")) {
            onSubscribe(connection, allArguments);
        }
        if (method.getName().contains("start")) {
            onStart(connection, result);
        }

    }


    private void onSubscribe(DefaultConsumerConnection connection, Object[] allArguments) {
        DefaultMQPushConsumer originalConsumer = connection.getDelegate();
        if (connection.getOriginalConsumerGroup() == null) {
            connection.setOriginalConsumerGroup(originalConsumer.getConsumerGroup());
        }
        String topic = (String) allArguments[0];
        String tags = (String) allArguments[1];
        Topic tp = new Topic();
        tp.setTopic(topic);
        tp.setTags(tags);
        connection.addTopic(tp);
    }


    private void onStart(DefaultConsumerConnection connection, MethodInterceptResult result) throws MQClientException {
        //不调用start方法,由内容的实现调用
        result.defineReturnValue(null);
        //构建实际的订阅
        connection.buildAndStartConsumers();
    }


}
