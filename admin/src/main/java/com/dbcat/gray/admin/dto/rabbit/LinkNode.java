package com.dbcat.gray.admin.dto.rabbit;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LinkNode {

    @JsonProperty("peer_addr")
    private String peerAddr;
    @JsonProperty("peer_port")
    private Integer peerPort;
    @JsonProperty("sock_addr")
    private String sockAddr;
    @JsonProperty("sock_port")
    private Integer sockPort;
    private String type;

    public String getPeerAddr() {
        return peerAddr;
    }

    public void setPeerAddr(String peerAddr) {
        this.peerAddr = peerAddr;
    }

    public Integer getPeerPort() {
        return peerPort;
    }

    public void setPeerPort(Integer peerPort) {
        this.peerPort = peerPort;
    }

    public String getSockAddr() {
        return sockAddr;
    }

    public void setSockAddr(String sockAddr) {
        this.sockAddr = sockAddr;
    }

    public Integer getSockPort() {
        return sockPort;
    }

    public void setSockPort(Integer sockPort) {
        this.sockPort = sockPort;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
