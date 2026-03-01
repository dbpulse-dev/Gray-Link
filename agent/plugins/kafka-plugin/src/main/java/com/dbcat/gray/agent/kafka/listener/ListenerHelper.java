package com.dbcat.gray.agent.kafka.listener;

import com.dbcat.gray.agent.core.AppInstance;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.springframework.kafka.listener.*;

import java.util.List;
import java.util.stream.Collectors;

import static com.dbcat.gray.agent.core.conf.Config.Agent.ROUTE_LABEL;

/**
 * @author Blackfost
 */
public class ListenerHelper {

    public static boolean filter(ConsumerRecord data, boolean isGray) {
        if(AppInstance.isDoubleSubscript()){
            return true;
        }
        Header xEnv = data.headers().lastHeader(ROUTE_LABEL);
        if (xEnv != null && isGray) {
            return true;
        }
        if (xEnv == null && !isGray) {
            return true;
        }
        return false;
    }

    public static <K, V> List<ConsumerRecord<K, V>> filter(List<ConsumerRecord<K, V>> consumerRecords, boolean isGray) {
        if(AppInstance.isDoubleSubscript()){
            return consumerRecords;
        }
        if (isGray) {
            return consumerRecords.stream().filter(d -> d.headers().lastHeader(ROUTE_LABEL) != null).collect(Collectors.toList());
        }
        return consumerRecords.stream().filter(d -> d.headers().lastHeader(ROUTE_LABEL) == null).collect(Collectors.toList());

    }


    public static GenericMessageListener buildListenerFilter(Object delegate, boolean isGray) {
        if (delegate instanceof AcknowledgingConsumerAwareMessageListener) {
            return new AcknowledgingConsumerAwareMessageListenerFilter((GenericMessageListener) delegate, isGray);
        }
        if (delegate instanceof BatchAcknowledgingConsumerAwareMessageListener) {
            return new BatchAcknowledgingConsumerAwareMessageListenerFilter((BatchMessageListener) delegate, isGray);
        }
        if (delegate instanceof ConsumerAwareMessageListener) {
            return new ConsumerAwareMessageListenerFilter((GenericMessageListener) delegate, isGray);
        }
        if (delegate instanceof BatchConsumerAwareMessageListener) {
            return new BatchConsumerAwareMessageListenerFilter((BatchMessageListener) delegate, isGray);
        }
        if (delegate instanceof AcknowledgingMessageListener) {
            return new AcknowledgingMessageListenerFilter((GenericMessageListener) delegate, isGray);
        }
        if (delegate instanceof BatchAcknowledgingMessageListener) {
            return new BatchAcknowledgingMessageListenerFilter((BatchMessageListener) delegate, isGray);
        }
        return new GenericMessageListenerFilter((GenericMessageListener) delegate, isGray);

    }
}
