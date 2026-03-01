package com.dbcat.gray.agent.rabbit;


import com.dbcat.gray.agent.core.mq.MQConnectionManager;
import com.dbcat.gray.agent.core.plugin.interceptor.enhance.EnhancedInstance;
import com.dbcat.gray.agent.core.plugin.interceptor.enhance.InstanceConstructorInterceptor;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;

/**
 * @author Blackfost
 */
public class CachingConnectionFactoryInterceptor implements InstanceConstructorInterceptor {

    @Override
    public void onConstruct(EnhancedInstance objInst, Object[] allArguments) throws Throwable {
        //可能会重入个构造，只需存一下
        if (objInst instanceof GCachingConnectionFactory) {
            return;
        }
        CachingConnectionFactory cacheConnectionFactory = (CachingConnectionFactory) objInst;
        RabbitMQConnection mqConnection = new RabbitMQConnection("rabbitMQ");
        mqConnection.setDelegate(cacheConnectionFactory);
        boolean add = MQConnectionManager.add(mqConnection);
        if (add) {
            objInst.setGrayDynamicField(mqConnection);
        }
    }
}

