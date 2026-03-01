package com.dbcat.gray.admin.dto.rabbit;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;

public class RabbitQueueBinding {


    @JsonProperty("arguments")
    private HashMap<String, Object> arguments;

    @JsonProperty("source")
    private String exchange;

    private String vhost;

    /**
     * 队列名称
     */
    private String destination;

    @JsonProperty("destination_type")
    private String destinationType;

    @JsonProperty("routing_key")
    private String routingKey;

    @JsonProperty("properties_key")
    private String propertiesKey;


    public HashMap<String, Object> getArguments() {
        return arguments;
    }

    public void setArguments(HashMap<String, Object> arguments) {
        this.arguments = arguments;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getVhost() {
        return vhost;
    }

    public void setVhost(String vhost) {
        this.vhost = vhost;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDestinationType() {
        return destinationType;
    }

    public void setDestinationType(String destinationType) {
        this.destinationType = destinationType;
    }

    public String getRoutingKey() {
        return routingKey;
    }

    public void setRoutingKey(String routingKey) {
        this.routingKey = routingKey;
    }

    public String getPropertiesKey() {
        return propertiesKey;
    }

    public void setPropertiesKey(String propertiesKey) {
        this.propertiesKey = propertiesKey;
    }
}