package com.dbcat.gray.agent.rabbit;


import com.dbcat.gray.agent.core.util.GrayUtils;
import com.dbcat.gray.agent.core.util.ReflectUtils;
import com.rabbitmq.client.Address;
import org.springframework.amqp.rabbit.connection.AbstractConnectionFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * @author Blackfost
 */
public class ConnectionFactoryManager {


    private static Map<CachingConnectionFactory/*oriFactory*/, CachingConnectionFactory> normalFactories = new ConcurrentHashMap<>();

    private static Map<CachingConnectionFactory/*oriFactory*/, CachingConnectionFactory> grayFactories = new ConcurrentHashMap<>();


    public static ConnectionFactory getFactory(CachingConnectionFactory oriFactory, boolean isGray) {
        return isGray ? getGrayFactory(oriFactory) : getNormalFactory(oriFactory);
    }

    private static ConnectionFactory getNormalFactory(CachingConnectionFactory oriFactory) {
        CachingConnectionFactory normalFactory = normalFactories.get(oriFactory);
        if (normalFactory != null) {
            return normalFactory;
        }
        synchronized (ConnectionFactoryManager.class) {
            if (normalFactory != null) {
                return normalFactory;
            }
            normalFactory = buildConnectionFactory(oriFactory, 1, connectionFactory -> new GCachingConnectionFactory(connectionFactory));
            normalFactories.put(oriFactory, normalFactory);
            return normalFactory;
        }
    }

    private static ConnectionFactory getGrayFactory(CachingConnectionFactory oriFactory) {
        CachingConnectionFactory grayFactory = grayFactories.get(oriFactory);
        if (grayFactory != null) {
            return grayFactory;
        }
        synchronized (ConnectionFactoryManager.class) {
            if (grayFactory != null) {
                return grayFactory;
            }
            grayFactory = buildConnectionFactory(oriFactory, 0, connectionFactory -> new GCachingConnectionFactory(connectionFactory));
            grayFactories.put(oriFactory, grayFactory);
            return grayFactory;
        }
    }

    private static CachingConnectionFactory buildConnectionFactory(CachingConnectionFactory oriFactory, int envStatus, Function<com.rabbitmq.client.ConnectionFactory, CachingConnectionFactory> fnType) {
        GConnectionFactory grayRabbit = buildRabbitFactor(oriFactory, envStatus);
        CachingConnectionFactory targetFactory = fnType.apply(grayRabbit);
        targetFactory.setAddresses(grayRabbit.getHost() + ":" + grayRabbit.getPort());
        targetFactory.setCacheMode(oriFactory.getCacheMode());
        targetFactory.setConnectionCacheSize(oriFactory.getConnectionCacheSize());
        targetFactory.setPublisherReturns(oriFactory.isPublisherReturns());
        try {
            CachingConnectionFactory.ConfirmType confirmType = (CachingConnectionFactory.ConfirmType) ReflectUtils.getFieldValue(oriFactory, "confirmType");
            targetFactory.setPublisherConfirmType(confirmType);
        } catch (NoSuchMethodError e) {
        }
        return targetFactory;
    }


    private static GConnectionFactory buildRabbitFactor(CachingConnectionFactory oriFactory, int envStatus) {
        GConnectionFactory grayRabbit = new GConnectionFactory();
        Address address = getAddress(oriFactory);
        com.rabbitmq.client.ConnectionFactory oriRabbit = oriFactory.getRabbitConnectionFactory();
        if (address != null) {
            grayRabbit.setHost(address.getHost());
            grayRabbit.setPort(address.getPort());
        } else {
            grayRabbit.setHost(oriRabbit.getHost());
            grayRabbit.setPort(oriRabbit.getPort());
        }
        String srcVirtualhost = oriFactory.getVirtualHost();
        String virtualHost = GrayUtils.rebuildName(srcVirtualhost, envStatus);
        grayRabbit.setVirtualHost(virtualHost);
        grayRabbit.setUsername(oriRabbit.getUsername());
        grayRabbit.setPassword(oriRabbit.getPassword());
        grayRabbit.setConnectionTimeout(oriRabbit.getConnectionTimeout());
        grayRabbit.setRequestedHeartbeat(oriRabbit.getRequestedHeartbeat());
        grayRabbit.setNetworkRecoveryInterval(oriRabbit.getNetworkRecoveryInterval());
        grayRabbit.setSaslConfig(oriRabbit.getSaslConfig());
        grayRabbit.setHandshakeTimeout(oriRabbit.getHandshakeTimeout());
        grayRabbit.setRecoveryDelayHandler(oriRabbit.getRecoveryDelayHandler());
        return grayRabbit;
    }

    private static Address getAddress(AbstractConnectionFactory factory) {
        Object objAddresses = ReflectUtils.getFieldValue(factory, "addresses");
        if (objAddresses == null) {
            String host = factory.getHost();
            return new Address(host, factory.getPort());
        }
        if (objAddresses instanceof Address[]) {
            Address[] addresses = (Address[]) objAddresses;
            return addresses[0];
        }
        if (objAddresses instanceof List) {
            List<Address> addresses = (List<Address>) objAddresses;
            return addresses.get(0);
        }
        return null;
    }

    /**
     * @param oriFactory
     */
    public static void resetConnection(CachingConnectionFactory oriFactory) {
        CachingConnectionFactory normalFactory = normalFactories.get(oriFactory);
        if (normalFactory != null) {
            normalFactory.resetConnection();
        }
        CachingConnectionFactory grayFactory = grayFactories.get(oriFactory);
        if (grayFactory != null) {
            grayFactory.resetConnection();
        }
    }

}
