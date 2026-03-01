package com.dbcat.gray.agent.rabbit.consumer;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.listener.BlockingQueueConsumer;

/**
 * @author Blackfost
 */
public class BlockingQueueConsumerHolder {

    private BlockingQueueConsumer consumer;
    private CachingConnectionFactory oriFactory;
    private String[] queues;
    private String queuesId;
    private boolean isGray;

    public BlockingQueueConsumerHolder(BlockingQueueConsumer consumer, CachingConnectionFactory oriFactory) {
        this.consumer = consumer;
        this.oriFactory = oriFactory;
    }

    public CachingConnectionFactory getOriFactory() {
        return oriFactory;
    }

    public BlockingQueueConsumer getConsumer() {
        return consumer;
    }

    public void setConsumer(BlockingQueueConsumer consumer) {
        this.consumer = consumer;
    }

    public String[] getQueues() {
        return queues;
    }

    public boolean isGray() {
        return isGray;
    }

    public void setGray(boolean gray) {
        isGray = gray;
    }

    public void setQueues(String[] queues) {
        this.queues = queues;
    }

    public String getQueuesId() {
        return queuesId;
    }

    public void setQueuesId(String queuesId) {
        this.queuesId = queuesId;
    }
}
