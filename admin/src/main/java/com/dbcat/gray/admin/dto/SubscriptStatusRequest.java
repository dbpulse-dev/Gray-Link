package com.dbcat.gray.admin.dto;


public class SubscriptStatusRequest {

    private String appName;

    private String ip;

    private int envStatus;

    public int getEnvStatus() {
        return envStatus;
    }

    public void setEnvStatus(int envStatus) {
        this.envStatus = envStatus;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }
}
