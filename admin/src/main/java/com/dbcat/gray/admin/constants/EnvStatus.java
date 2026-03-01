package com.dbcat.gray.admin.constants;

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
     * 空闲
     */
    IDLE(2);
    int code;

    EnvStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
