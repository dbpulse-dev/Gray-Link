package com.dbcat.gray.admin.dto;


import java.io.Serializable;
import java.util.List;

public class AppStatusBatchUpdate implements Serializable {

    private List<String> appNames;


    /**
     * 环境状态，0 灰度状态,1正常状态,2备份状态
     */
    private int envStatus;


    public List<String> getAppNames() {
        return appNames;
    }

    public void setAppNames(List<String> appNames) {
        this.appNames = appNames;
    }

    public int getEnvStatus() {
        return envStatus;
    }

    public void setEnvStatus(int envStatus) {
        this.envStatus = envStatus;
    }
}
