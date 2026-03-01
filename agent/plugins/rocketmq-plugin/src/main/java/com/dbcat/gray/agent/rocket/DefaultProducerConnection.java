package com.dbcat.gray.agent.rocket;


import com.dbcat.gray.agent.core.mq.AbstractMQConnection;
import org.apache.rocketmq.client.producer.DefaultMQProducer;

/**
 * @author Blackfost
 */
public class DefaultProducerConnection extends AbstractMQConnection<DefaultMQProducer> {

    private boolean isInnerProducer;

    public DefaultProducerConnection(String type) {
        super(type);
    }

    public void setInnerProducer(boolean innerProducer) {
        isInnerProducer = innerProducer;
    }

    public boolean isInnerProducer() {
        return isInnerProducer;
    }

    @Override
    public boolean shouldReset() {
        return false;
    }

    @Override
    public boolean restart() {
        return true;
    }

    @Override
    public void onSubscriptChange() {

    }

    @Override
    public String description() {
        return delegate.getNamesrvAddr();
    }

    @Override
    public boolean isFast() {
        return false;
    }
}
