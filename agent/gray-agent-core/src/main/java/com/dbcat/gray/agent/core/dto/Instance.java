package com.dbcat.gray.agent.core.dto;

import java.io.Serializable;
import java.util.Date;

public class Instance implements Serializable {

    private String appName;

    private String uuid;

    private int status;
    /**
     * 环境状态: 0灰度状态,1正常状态,2备份状态(不会被路由到)
     */
    private int envStatus;

    /**
     * 更新时间
     */
    private Date updateAt;

    /**
     * 有效时间，服务端会定期更新，如果没有更新从缓存中删除
     */
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getEnvStatus() {
        return envStatus;
    }

    public void setEnvStatus(int envStatus) {
        this.envStatus = envStatus;
    }

    public Date getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Date updateAt) {
        this.updateAt = updateAt;
    }

    public long getExpireAt() {
        return expireAt;
    }

    public void setExpireAt(long expireAt) {
        this.expireAt = expireAt;
    }
}
