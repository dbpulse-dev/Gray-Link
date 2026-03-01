package com.dbcat.gray.admin.dto;


import java.util.Date;

public class AppInstanceResponse {

    private Integer id;

    private String appName;

    private String uuid;

    /**
     * 0启动中，1运行中，2已停
     */
    private Integer status;


    private Date startAt;

    private Date reportAt;

    private Date checkAt;

    private Integer envStatus;

    private Integer subscriptStatus;

    private Integer agentPort;

    private String hostname;

    private String ip;

    public Integer getSubscriptStatus() {
        return subscriptStatus;
    }

    public void setSubscriptStatus(Integer subscriptStatus) {
        this.subscriptStatus = subscriptStatus;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Date getReportAt() {
        return reportAt;
    }

    public void setReportAt(Date reportAt) {
        this.reportAt = reportAt;
    }

    public Date getCheckAt() {
        return checkAt;
    }

    public void setCheckAt(Date checkAt) {
        this.checkAt = checkAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getStartAt() {
        return startAt;
    }

    public void setStartAt(Date startAt) {
        this.startAt = startAt;
    }

    public Integer getEnvStatus() {
        return envStatus;
    }

    public void setEnvStatus(Integer envStatus) {
        this.envStatus = envStatus;
    }

    public Integer getAgentPort() {
        return agentPort;
    }

    public void setAgentPort(Integer agentPort) {
        this.agentPort = agentPort;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }
}
