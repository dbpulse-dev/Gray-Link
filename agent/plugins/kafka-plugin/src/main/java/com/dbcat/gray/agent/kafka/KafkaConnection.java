package com.dbcat.gray.agent.kafka;

import com.dbcat.gray.agent.core.AppInstance;
import com.dbcat.gray.agent.core.mq.AbstractMQConnection;
import com.dbcat.gray.agent.core.util.GrayUtils;
import com.dbcat.gray.agent.core.util.ReflectUtils;
import com.dbcat.gray.agent.kafka.listener.ListenerHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Blackfost
 */
public class KafkaConnection extends AbstractMQConnection<AbstractMessageListenerContainer> {


    private DefaultKafkaConsumerFactory origConsumerFactory;


    private GConcurrentMessageListenerContainer grayConsumer;
    private GConcurrentMessageListenerContainer normalConsumer;

    public KafkaConnection(String type, DefaultKafkaConsumerFactory origConsumerFactory) {
        super(type);
        this.origConsumerFactory = origConsumerFactory;
    }


    @Override
    public boolean restart() {
        this.shutdown();
        this.buildConsumers();
        this.start();
        return true;
    }

    @Override
    public void onSubscriptChange() {
        AppInstance instance = AppInstance.getInstance();
        if (AppInstance.isGraySubscript()) {
            //仅订阅灰度消息
            if (grayConsumer == null) {
                grayConsumer = buildConsumer(0);
                grayConsumer.start();
            }
            if (normalConsumer != null && normalConsumer.isRunning()) {
                normalConsumer.stop();
                normalConsumer = null;
            }
        } else  {
            //双订阅或仅订阅都用正常订阅消息
            if (normalConsumer == null) {
                normalConsumer = buildConsumer(1);
                normalConsumer.start();
            }
            if (grayConsumer != null && grayConsumer.isRunning()) {
                grayConsumer.stop();
                grayConsumer = null;
            }
        }
    }

    public synchronized void buildConsumers() {
        AppInstance instance = AppInstance.getInstance();
        if (instance.getSubscriptStatus() == 0) {
            //仅订阅灰度消息
            grayConsumer = buildConsumer(0);
        } else if (instance.getSubscriptStatus() == 1) {
            //仅订阅正常消息
            normalConsumer = buildConsumer(1);
        } else {
            //双订阅，仅建一个订阅组,接收所有消息
            normalConsumer = buildConsumer(1);
        }
    }

    @Override
    public String description() {
        return "kafka-client";
    }

    private void shutdown() {
        //不同版stop方法有差异
        if (grayConsumer != null && grayConsumer.isRunning()) {
            grayConsumer.stop();
            grayConsumer = null;
        }
        if (normalConsumer != null && normalConsumer.isRunning()) {
            normalConsumer.stop();
            normalConsumer = null;
        }
    }

    public void start() {
        if (grayConsumer != null) {
            grayConsumer.start();
        }
        if (normalConsumer != null) {
            normalConsumer.start();
        }
    }




    private GConcurrentMessageListenerContainer buildConsumer(int envStatus) {
        Map<String, Object> origConfig = origConsumerFactory.getConfigurationProperties();
        String origGroupId = (String) origConfig.get("group.id");
        String targetGroupId = GrayUtils.rebuildName(origGroupId, envStatus);
        Map<String, Object> consumerProperties = new HashMap<>(origConfig);
        consumerProperties.put("group.id", targetGroupId);
        consumerProperties.put("AGENT_IN_CREATE", "AGENT_IN_CREATE");

        DefaultKafkaConsumerFactory consumerFactory = new DefaultKafkaConsumerFactory(consumerProperties);
        ContainerProperties containerProperties = buildContainerProperties(envStatus);
        GConcurrentMessageListenerContainer listenerContainer = new GConcurrentMessageListenerContainer(consumerFactory, containerProperties);
        listenerContainer.setApplicationEventPublisher(delegate.getApplicationEventPublisher());

        Object applicationContext = ReflectUtils.getFieldValue(delegate, "applicationContext");
        listenerContainer.setApplicationContext((ApplicationContext) applicationContext);
        Object afterRollbackProcessor = ReflectUtils.getFieldValue(delegate, "afterRollbackProcessor");
        listenerContainer.setAfterRollbackProcessor((AfterRollbackProcessor) afterRollbackProcessor);
        Object autoStartup = ReflectUtils.getFieldValue(delegate, "autoStartup");

        listenerContainer.setAutoStartup((Boolean) autoStartup);
        Object recordInterceptor = ReflectUtils.getFieldValue(delegate, "recordInterceptor");
        listenerContainer.setRecordInterceptor((RecordInterceptor) recordInterceptor);
        return listenerContainer;
    }


    private ContainerProperties buildContainerProperties(int envStatus) {
        ContainerProperties origContainerProperties = delegate.getContainerProperties();
        ContainerProperties containerProperties = new ContainerProperties(origContainerProperties.getTopics());
        BeanUtils.copyProperties(origContainerProperties, containerProperties, "topics", "topicPartitions", "topicPattern", "ackCount", "ackTime");
        if (containerProperties.getAckCount() > 0) {
            containerProperties.setAckCount(origContainerProperties.getAckCount());
        }
        if (containerProperties.getAckTime() > 0) {
            containerProperties.setAckTime(origContainerProperties.getAckTime());
        }
        Object messageListener = containerProperties.getMessageListener();
        GenericMessageListener genericMessageListener = ListenerHelper.buildListenerFilter(messageListener, envStatus == 0);
        containerProperties.setMessageListener(genericMessageListener);
        return containerProperties;
    }
}
