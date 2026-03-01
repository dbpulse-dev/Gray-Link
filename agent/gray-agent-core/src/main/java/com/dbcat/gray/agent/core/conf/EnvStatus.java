package com.dbcat.gray.agent.core.conf;
public enum EnvStatus {
    /**
     * 灰度
     */
    GRAY(0),
    /**
     * 正常
     */
    NORMAL(1),
    /**
     * 备分
     */
    BACKUP(2);
    int code;

    EnvStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}