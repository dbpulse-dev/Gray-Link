package com.dbcat.gray.agent.core.mq;

/**
 * @param <C>
 * @author Blackfost
 */
public interface MQConnection<C> {


    void setDelegate(C connection);

    /**
     * 强制重连
     */
    boolean restart();

    /**
     * 订阅变更
     */
    void onSubscriptChange();

    default boolean shouldReset() {
        return true;
    }

    String description();


    default int order() {
        return 0;
    }

    default boolean isFast() {
        return true;
    }

    String getType();

}
