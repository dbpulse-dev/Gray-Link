package com.dbcat.gray.agent.core.dto;


import java.io.Serializable;


public class ExecuteResponse implements Serializable {
    private String type;
    private String name;
    private long timeCost;
    private String message;
    private int code = 200;
    private boolean isGrayStatus;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTimeCost() {
        return timeCost;
    }

    public void setTimeCost(long timeCost) {
        this.timeCost = timeCost;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean isGrayStatus() {
        return isGrayStatus;
    }

    public void setGrayStatus(boolean grayStatus) {
        isGrayStatus = grayStatus;
    }
}
