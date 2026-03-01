package com.dbcat.gray.admin.dto;


import java.io.Serializable;

public class AppStatus implements Serializable {


    /**
     * 当前应用灰度实例数
     */
    private int grays;

    /**
     * 当前应用正常实例数
     */
    private int normals;


    /**
     * 双订阅开关，更新是否有效;0 信息无效这例无任务改变,1 信息有效
     */
    private int enable;

    public int getGrays() {
        return grays;
    }

    public void setGrays(int grays) {
        this.grays = grays;
    }

    public int getNormals() {
        return normals;
    }

    public void setNormals(int normals) {
        this.normals = normals;
    }

    public int getEnable() {
        return enable;
    }

    public void setEnable(int enable) {
        this.enable = enable;
    }
}
