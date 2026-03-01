package com.dbcat.gray.agent.core.mq;

import com.dbcat.gray.agent.core.dto.ComponentType;
import com.dbcat.gray.agent.core.dto.ConsumerMessage;
import com.dbcat.gray.agent.core.dto.PublishMessage;

public interface MQListener<P, C> {

    ComponentType type();

    void onPublish(PublishMessage<P> publishMessage);

    void onConsume(ConsumerMessage<C> consumerMessage);
}
