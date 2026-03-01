package com.dbcat.gray.admin.dto.rabbit;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;

public class RabbitQueue {

    /**
     * 队列名称
     */
    private String name;

    @JsonProperty("vhost")
    private String virtualHost;

    @JsonProperty("auto_delete")
    private boolean autoDelete;

    private boolean durable;

    private String type;

    private String node;

    @JsonProperty("arguments")
    private HashMap<String, Object> arguments;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVirtualHost() {
        return virtualHost;
    }

    public void setVirtualHost(String virtualHost) {
        this.virtualHost = virtualHost;
    }

    public boolean isAutoDelete() {
        return autoDelete;
    }

    public void setAutoDelete(boolean autoDelete) {
        this.autoDelete = autoDelete;
    }

    public boolean isDurable() {
        return durable;
    }

    public void setDurable(boolean durable) {
        this.durable = durable;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }

    public HashMap<String, Object> getArguments() {
        return arguments;
    }

    public void setArguments(HashMap<String, Object> arguments) {
        this.arguments = arguments;
    }
}
