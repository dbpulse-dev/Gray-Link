package com.dbcat.gray.agent.core.dto;

public class KafkaPublish extends PublishCommon {

    private String topic;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
