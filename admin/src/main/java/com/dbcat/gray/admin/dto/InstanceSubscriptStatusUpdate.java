package com.dbcat.gray.admin.dto;


import java.io.Serializable;

public class InstanceSubscriptStatusUpdate implements Serializable {

    private Integer id;

    /**
     * 0:仅订阅灰度消息，1仅订阅正常消息，2同时订阅正常和灰度消息
     */
    private int subscriptStatus;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getSubscriptStatus() {
        return subscriptStatus;
    }

    public void setSubscriptStatus(int subscriptStatus) {
        this.subscriptStatus = subscriptStatus;
    }
}
