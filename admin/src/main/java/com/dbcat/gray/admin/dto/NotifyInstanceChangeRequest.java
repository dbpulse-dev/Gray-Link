package com.dbcat.gray.admin.dto;


import java.io.Serializable;

public class NotifyInstanceChangeRequest implements Serializable {

    private String appName;

    private String uuid;

    private String ip;

    /**
     * 状态；0:启动中,1：运行中，2：停止
     */
    private int status;

    /**
     * 环境状态，1正常状态,0 灰度状态
     */
    private int envStatus;

    private long expireAt;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getEnvStatus() {
        return envStatus;
    }

    public void setEnvStatus(int envStatus) {
        this.envStatus = envStatus;
    }

    public long getExpireAt() {
        return expireAt;
    }

    public void setExpireAt(long expireAt) {
        this.expireAt = expireAt;
    }
}
