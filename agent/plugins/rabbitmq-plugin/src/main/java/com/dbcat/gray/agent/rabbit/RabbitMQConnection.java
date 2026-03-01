package com.dbcat.gray.agent.rabbit;


import com.dbcat.gray.agent.core.AppInstance;
import com.dbcat.gray.agent.core.logging.api.ILog;
import com.dbcat.gray.agent.core.logging.api.LogManager;
import com.dbcat.gray.agent.core.mq.AbstractMQConnection;
import com.dbcat.gray.agent.core.util.ServerContextHolder;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;

import static com.dbcat.gray.agent.core.conf.Config.Agent.ROUTE_LABEL;
import static com.dbcat.gray.agent.rabbit.ConnectionFactoryManager.getFactory;

/**
 * @author Blackfost
 */
public class RabbitMQConnection extends AbstractMQConnection<CachingConnectionFactory> {

    private static final ILog logger = LogManager.getLogger(RabbitMQConnection.class);


    private String address;

    public RabbitMQConnection(String type) {
        super(type);
    }

    @Override
    public boolean restart() {
        //存在缓存连接 这个virtualHost可能与缓存连接不一致,直接触发好了
        logger.info("灰度状态发生变化,重连 rabbitmq:{}", delegate.getVirtualHost());
        ConnectionFactoryManager.resetConnection(delegate);
        return true;
    }

    @Override
    public void onSubscriptChange() {
        this.restart();
    }


    @Override
    public String description() {
        return delegate.getVirtualHost();
    }


    @Override
    public int order() {
        return 10;
    }

    public ConnectionFactory getPublishConnectionFactory() {
        Object xEnv = ServerContextHolder.get(ROUTE_LABEL);
        return getFactory(delegate, xEnv != null);
    }

}
