package com.dbcat.gray.admin.dto;


import java.io.Serializable;


public class ServerDeployEnv implements Serializable {

    private int envStatus;

    private int enable;

    public int getEnvStatus() {
        return envStatus;
    }

    public void setEnvStatus(int envStatus) {
        this.envStatus = envStatus;
    }

    public int getEnable() {
        return enable;
    }

    public void setEnable(int enable) {
        this.enable = enable;
    }
}
