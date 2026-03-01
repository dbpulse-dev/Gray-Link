package com.dbcat.gray.agent.rabbit.consumer;

import com.dbcat.gray.agent.core.AppInstance;
import com.dbcat.gray.agent.core.plugin.interceptor.enhance.EnhancedInstance;
import com.dbcat.gray.agent.core.util.ReflectUtils;
import com.dbcat.gray.agent.core.util.SignatureUtils;
import com.dbcat.gray.agent.rabbit.ConnectionFactoryManager;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.BlockingQueueConsumer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Blackfost
 */
public class BlockingQueueManager {

    private static Map<String, Set<BlockingQueueConsumerHolder>> consumers = new ConcurrentHashMap<>();

    public static BlockingQueueConsumerHolder bindSubscript(BlockingQueueConsumer consumer, String[] queues, CachingConnectionFactory connectionFactory) {
        String queuesId = SignatureUtils.createMd5(Arrays.asList(queues).toString());
        BlockingQueueConsumerHolder holder = new BlockingQueueConsumerHolder(consumer, connectionFactory);
        holder.setQueues(queues);
        holder.setQueuesId(queuesId);
        bindSubscriptFactory(holder);
        return holder;
    }


    /**
     * 绑定消息对应状态的消息
     *
     * @param holder
     */
    private static void bindSubscriptFactory(BlockingQueueConsumerHolder holder) {
        CachingConnectionFactory oriFactory = holder.getOriFactory();
        BlockingQueueConsumer queueConsumer = holder.getConsumer();
        if (!AppInstance.isDoubleSubscript()) {
            //仅订阅一种消息
            holder.setGray(AppInstance.isGraySubscript());
            ConnectionFactory grayFactory = ConnectionFactoryManager.getFactory(oriFactory, AppInstance.isGraySubscript());
            ReflectUtils.setFieldValue(queueConsumer, "connectionFactory", grayFactory);
            return;
        }
        ConnectionFactory factory = null;
        if (AppInstance.isGrayStatus()) {
            //灰度实例
            if (!hasSubscript(holder.getQueuesId(), false)) {
                //未订阅正常消息，仅设一个订阅通道
                factory = ConnectionFactoryManager.getFactory(oriFactory, false);
            } else {
                factory = ConnectionFactoryManager.getFactory(oriFactory, true);
                holder.setGray(true);
            }
        } else {
            //正常实例
            if (!hasSubscript(holder.getQueuesId(), true)) {
                //未订阅灰度消息，仅设一个订阅通道
                factory = ConnectionFactoryManager.getFactory(oriFactory, true);
                holder.setGray(true);
            } else {
                factory = ConnectionFactoryManager.getFactory(oriFactory, false);
            }
        }
        //给consumer绑定相应类型的消息
        ReflectUtils.setFieldValue(queueConsumer, "connectionFactory", factory);
    }


    public static void add(BlockingQueueConsumerHolder consumerHolder) {
        Set<BlockingQueueConsumerHolder> blockingQueueConsumerHolders = consumers.computeIfAbsent(consumerHolder.getQueuesId(), s -> new HashSet<>());
        blockingQueueConsumerHolders.add(consumerHolder);
    }

    public static void remove(BlockingQueueConsumer consumer) {
        EnhancedInstance objInst = (EnhancedInstance) consumer;
        BlockingQueueConsumerHolder consumerHolder = (BlockingQueueConsumerHolder) objInst.getGrayDynamicField();
        Set<BlockingQueueConsumerHolder> blockingQueueConsumerHolders = consumers.get(consumerHolder.getQueuesId());
        if (blockingQueueConsumerHolders == null) {
            return;
        }
        blockingQueueConsumerHolders.remove(consumerHolder);
    }


    private static boolean hasSubscript(String queuesId, boolean isGray) {
        Set<BlockingQueueConsumerHolder> blockingQueueConsumerHolders = consumers.get(queuesId);
        if (blockingQueueConsumerHolders == null) {
            return false;
        }
        for (BlockingQueueConsumerHolder holder : blockingQueueConsumerHolders) {
            if (holder.isGray() == isGray) {
                return true;
            }
        }
        return false;
    }
}
