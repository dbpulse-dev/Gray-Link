package com.dbcat.gray.agent.core.dto;

public enum ComponentType {

    RABBIT("rabbit"),
    ROCKET("rocket"),
    KAFKA("kafka"),
    API("api");
    private String type;


    ComponentType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }


}
