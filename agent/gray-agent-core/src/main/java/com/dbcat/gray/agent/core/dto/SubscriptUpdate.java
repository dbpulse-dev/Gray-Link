package com.dbcat.gray.agent.core.dto;


import java.io.Serializable;


public class SubscriptUpdate implements Serializable {


    /**
     * 0:仅订阅灰度消息，1仅订阅正常消息，2同时订阅正常和灰度消息
     */
    private int subscriptStatus;

    private int enable;

    /**
     * strategy 0同步方式，1异步方式，2混合模式(快的用同步，慢的用异步)，3混合同步并发(快的同步，慢的并发同步)
     */
    private int strategy = 3;

    public int getStrategy() {
        return strategy;
    }

    public void setStrategy(int strategy) {
        this.strategy = strategy;
    }

    public int getEnable() {
        return enable;
    }

    public void setEnable(int enable) {
        this.enable = enable;
    }

    public int getSubscriptStatus() {
        return subscriptStatus;
    }

    public void setSubscriptStatus(int subscriptStatus) {
        this.subscriptStatus = subscriptStatus;
    }
}
