package com.dbcat.gray.admin.entity;


import java.util.Date;


public class AppInstanceEntity {

    private Integer id;


    private String appName;


    private String hostname;

    private String ip;

    private String env;

    private Integer agentPort;

    /**
     * 状态；1：运行中，2：停止，3：异常
     */
    private Integer status;

    private Integer envStatus;

    private Integer subscriptStatus;

    private String uuid;

    private Date startAt;

    private Date reportAt;

    private Date checkAt;

    private String version;

    private String agentVersion;

    public Integer getSubscriptStatus() {
        return subscriptStatus;
    }

    public void setSubscriptStatus(Integer subscriptStatus) {
        this.subscriptStatus = subscriptStatus;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getAgentVersion() {
        return agentVersion;
    }

    public void setAgentVersion(String agentVersion) {
        this.agentVersion = agentVersion;
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

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public Integer getAgentPort() {
        return agentPort;
    }

    public void setAgentPort(Integer agentPort) {
        this.agentPort = agentPort;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getEnvStatus() {
        return envStatus;
    }

    public void setEnvStatus(Integer envStatus) {
        this.envStatus = envStatus;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Date getStartAt() {
        return startAt;
    }

    public void setStartAt(Date startAt) {
        this.startAt = startAt;
    }
}
