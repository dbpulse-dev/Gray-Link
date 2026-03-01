package com.dbcat.gray.agent.rabbit;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;

/**
 * @author Blackfost
 */
public class GCachingConnectionFactory extends CachingConnectionFactory {

    public GCachingConnectionFactory(com.rabbitmq.client.ConnectionFactory rabbitConnectionFactory) {
        super(rabbitConnectionFactory);
    }
}
