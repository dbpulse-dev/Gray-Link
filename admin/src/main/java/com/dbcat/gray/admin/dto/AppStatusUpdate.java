package com.dbcat.gray.admin.dto;


import java.io.Serializable;

public class AppStatusUpdate implements Serializable {

    private String appName;


    /**
     * 环境状态，0 灰度状态,1正常状态,2备份状态(不再接收流量)
     */
    private int envStatus;


    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public int getEnvStatus() {
        return envStatus;
    }

    public void setEnvStatus(int envStatus) {
        this.envStatus = envStatus;
    }
}
