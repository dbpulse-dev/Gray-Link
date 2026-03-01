package com.dbcat.gray.admin.dto.rabbit;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.HashMap;

public class RabbitExchange implements Serializable {


    @JsonProperty("arguments")
    private HashMap<String, Object> arguments;

    @JsonProperty("auto_delete")
    private String autoDelete;

    private boolean durable;

    private boolean internal;

    private String name;

    private String type;

    private String vhost;

    public HashMap<String, Object> getArguments() {
        return arguments;
    }

    public void setArguments(HashMap<String, Object> arguments) {
        this.arguments = arguments;
    }

    public String getAutoDelete() {
        return autoDelete;
    }

    public void setAutoDelete(String autoDelete) {
        this.autoDelete = autoDelete;
    }

    public boolean isDurable() {
        return durable;
    }

    public void setDurable(boolean durable) {
        this.durable = durable;
    }

    public boolean isInternal() {
        return internal;
    }

    public void setInternal(boolean internal) {
        this.internal = internal;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVhost() {
        return vhost;
    }

    public void setVhost(String vhost) {
        this.vhost = vhost;
    }
}