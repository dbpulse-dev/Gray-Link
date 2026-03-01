package com.dbcat.gray.admin.constants;

public enum SubscriptStatus {
    /**
     * 灰度订阅
     */
    GRAY(0),
    /**
     * 正常订阅
     */
    NORMAL(1),
    /**
     * 双订阅
     */
    DOUBLE(2);
    int status;

    SubscriptStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
