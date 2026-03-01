package com.dbcat.gray.admin.dto;


import java.io.Serializable;

public class InstanceStatusUpdate implements Serializable {

    private Integer id;


    /**
     * 环境状态，0 灰度状态,1正常状态,2备份状态
     */
    private int envStatus;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getEnvStatus() {
        return envStatus;
    }

    public void setEnvStatus(int envStatus) {
        this.envStatus = envStatus;
    }
}
