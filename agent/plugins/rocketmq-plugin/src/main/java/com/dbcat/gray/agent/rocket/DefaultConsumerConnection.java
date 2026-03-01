package com.dbcat.gray.agent.rocket;


import com.dbcat.gray.agent.core.AppInstance;
import com.dbcat.gray.agent.core.logging.api.ILog;
import com.dbcat.gray.agent.core.logging.api.LogManager;
import com.dbcat.gray.agent.core.mq.AbstractMQConnection;
import com.dbcat.gray.agent.core.util.GrayUtils;
import com.dbcat.gray.agent.rocket.listener.ListenerHelper;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.MessageSelector;
import org.apache.rocketmq.client.consumer.listener.MessageListener;
import org.apache.rocketmq.client.exception.MQClientException;

import java.util.ArrayList;
import java.util.List;

import static com.dbcat.gray.agent.core.conf.Config.Agent.MSG_FILTER_MODE;
import static com.dbcat.gray.agent.core.conf.Config.Agent.ROUTE_LABEL;

/**
 * @author Blackfost
 */
public class DefaultConsumerConnection extends AbstractMQConnection<DefaultMQPushConsumer> {

    private static final ILog logger = LogManager.getLogger(DefaultConsumerConnection.class);


    private String originalConsumerGroup;

    private List<Topic> originalTopics = new ArrayList<>();

    private DefaultMQPushConsumer normalConsumer;

    private DefaultMQPushConsumer grayConsumer;

    public DefaultConsumerConnection(String type) {
        super(type);
    }

    public void setOriginalConsumerGroup(String consumerGroup) {
        this.originalConsumerGroup = consumerGroup;
    }


    public String getOriginalConsumerGroup() {
        return originalConsumerGroup;
    }

    public void addTopic(Topic topic) {
        this.originalTopics.add(topic);
    }

    @Override
    public boolean restart() {
        this.shutdown();
        try {
            this.buildAndStartConsumers();
        } catch (MQClientException e) {
            logger.error("reset rocket mq failure", e);
            throw new RuntimeException(e);
        }
        return true;
    }

    @Override
    public void onSubscriptChange() {
        AppInstance instance = AppInstance.getInstance();
        try {
            if (instance.getSubscriptStatus() == 0) {
                //仅订阅灰度消息
                if (grayConsumer == null) {
                    grayConsumer = buildConsumer(0);
                    grayConsumer.start();
                }
                if (normalConsumer != null) {
                    normalConsumer.shutdown();
                    normalConsumer = null;
                }
            } else if (instance.getSubscriptStatus() == 1) {
                if (normalConsumer == null) {
                    normalConsumer = buildConsumer(1);
                    normalConsumer.start();
                }
                if (grayConsumer != null) {
                    grayConsumer.shutdown();
                    grayConsumer = null;
                }
            } else {
                //双订阅
                if (grayConsumer == null) {
                    grayConsumer = buildConsumer(0);
                    grayConsumer.start();
                }
                if (normalConsumer == null) {
                    normalConsumer = buildConsumer(1);
                    normalConsumer.start();
                }
            }
        } catch (Throwable t) {
            logger.error(" rocket subscript change failure", t);
            throw new RuntimeException(t);
        }

    }


    @Override
    public String description() {
        return delegate.getNamesrvAddr();
    }

    @Override
    public int order() {
        return -10;
    }


    @Override
    public boolean isFast() {
        return false;
    }


    public void buildAndStartConsumers() throws MQClientException {
        this.buildConsumers();
        this.start();
    }

    private void start() throws MQClientException {
        if (grayConsumer != null) {
            grayConsumer.start();
        }
        if (normalConsumer != null) {
            normalConsumer.start();
        }
    }

    public void shutdown() {
        if (grayConsumer != null) {
            grayConsumer.shutdown();
            grayConsumer = null;
        }
        if (normalConsumer != null) {
            normalConsumer.shutdown();
            normalConsumer = null;
        }
    }

    private void buildConsumers() throws MQClientException {
        AppInstance instance = AppInstance.getInstance();
        int subscriptType = instance.getSubscriptStatus();
        if (subscriptType == 0) {
            //仅订阅灰度消息
            this.grayConsumer = buildConsumer(0);
        } else if (subscriptType == 1) {
            //仅订阅正常消息
            this.normalConsumer = buildConsumer(1);
        } else {
            //双订阅，仅建一个订阅组,接收所有消息
            this.normalConsumer = buildConsumer(1);
        }
    }

    private DefaultMQPushConsumer buildConsumer(int envStatus) throws MQClientException {
        DefaultMQPushConsumer originalConsumer = delegate;
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("AGENT_IN_CREATE");
        String consumerGroup = GrayUtils.rebuildName(originalConsumer.getConsumerGroup(), envStatus);
        consumer.setConsumerGroup(consumerGroup);
        // 设置地址
        consumer.setNamesrvAddr(originalConsumer.getNamesrvAddr());
        consumer.setMaxReconsumeTimes(originalConsumer.getMaxReconsumeTimes());
        consumer.setConsumeThreadMax(originalConsumer.getConsumeThreadMax());
        consumer.setConsumeMessageBatchMaxSize(originalConsumer.getConsumeMessageBatchMaxSize());
        for (Topic tp : originalTopics) {
            subscriptByMode(consumer, tp, envStatus);
        }
        MessageListener messageListener = ListenerHelper.buildListenerFilter(delegate.getMessageListener(), envStatus);
        consumer.setMessageListener(messageListener);
        return consumer;
    }

    private void subscriptByMode(DefaultMQPushConsumer consumer, Topic tp, int envStatus) throws MQClientException {
        if(AppInstance.isDoubleSubscript()){
            //双订阅，不过消息消息
            consumer.subscribe(tp.getTopic(), tp.getTags());
            return;
        }
        String topic = tp.getTopic();
        if (MSG_FILTER_MODE == 0) {
            //客户端过滤
            consumer.subscribe(tp.getTopic(), tp.getTags());
            return;
        }
        //服务端过滤
        if (envStatus == 0) {
            consumer.subscribe(topic, MessageSelector.bySql(ROUTE_LABEL + " IS NOT NULL"));
        } else {
            consumer.subscribe(topic, MessageSelector.bySql(ROUTE_LABEL + " IS NULL"));
        }
    }

}
