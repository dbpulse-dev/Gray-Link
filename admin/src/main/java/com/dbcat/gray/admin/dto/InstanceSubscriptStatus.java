package com.dbcat.gray.admin.dto;


import java.io.Serializable;

public class InstanceSubscriptStatus implements Serializable {

    private int subscriptStatus;

    /**
     * 双订阅开关，更新是否有效;0 信息无效这例无任务改变,1 信息有效
     */
    private int enable;

    public int getSubscriptStatus() {
        return subscriptStatus;
    }

    public void setSubscriptStatus(int subscriptStatus) {
        this.subscriptStatus = subscriptStatus;
    }

    public int getEnable() {
        return enable;
    }

    public void setEnable(int enable) {
        this.enable = enable;
    }
}
