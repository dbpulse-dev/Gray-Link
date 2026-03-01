package com.dbcat.gray.agent.core.dto;

import java.io.Serializable;

public class Node implements Serializable {

    private String md5;
    /**
     * 域名或ip+端口
     */
    private String address;

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}