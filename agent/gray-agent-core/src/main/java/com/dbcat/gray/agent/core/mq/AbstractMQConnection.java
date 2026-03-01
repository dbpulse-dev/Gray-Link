package com.dbcat.gray.agent.core.mq;

public abstract class AbstractMQConnection<C> implements MQConnection<C> {

    protected volatile C delegate;

    protected String type;


    public AbstractMQConnection(String type) {
        this.type = type;
    }


    @Override
    public void setDelegate(C delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof AbstractMQConnection) {
            AbstractMQConnection c = (AbstractMQConnection) o;
            return this.delegate.equals(c.delegate);
        }
        return false;
    }

    public C getDelegate() {
        return delegate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
