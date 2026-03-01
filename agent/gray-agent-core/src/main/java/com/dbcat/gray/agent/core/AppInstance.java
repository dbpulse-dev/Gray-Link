package com.dbcat.gray.agent.core;

import com.dbcat.gray.agent.core.conf.Config;
import com.dbcat.gray.agent.core.util.OSUtil;
import com.dbcat.gray.agent.core.util.SignatureUtils;

/**
 * @author Blackfost
 */
public class AppInstance {

    private static AppInstance instance;

    private String appName;
    private String hostname;
    private String uuid;
    private String ip;
    private Integer agentPort;

    private String agentVersion;

    private String version;

    private long startAt;

    private int status = 1;

    /**
     * 环境状态，0 灰度状态,1正常状态,2备份状态
     */
    private int envStatus = 1;

    /**
     * 0:仅订阅灰度消息，1仅订阅正常消息，2同时订阅正常和灰度消息
     */
    private int subscriptStatus;

    private String env;


    /**
     * 消息消费模式
     * 0 不强制消费
     * 1 强制消费(如果没有灰度或正常实例，使用强制消费消息)
     */
    private int messageConsumeMode = 1;


    public int getStatus() {
        return status;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getMessageConsumeMode() {
        return messageConsumeMode;
    }

    public void setMessageConsumeMode(int messageConsumeMode) {
        this.messageConsumeMode = messageConsumeMode;
    }

    public int getEnvStatus() {
        return envStatus;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public void setEnvStatus(int envStatus) {
        this.envStatus = envStatus;
    }


    public long getStartAt() {
        return startAt;
    }

    public void setStartAt(long startAt) {
        this.startAt = startAt;
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

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getAgentPort() {
        return agentPort;
    }

    public void setAgentPort(Integer agentPort) {
        this.agentPort = agentPort;
    }

    public String getAgentVersion() {
        return agentVersion;
    }

    public void setAgentVersion(String agentVersion) {
        this.agentVersion = agentVersion;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getSubscriptStatus() {
        return subscriptStatus;
    }

    public void setSubscriptStatus(int subscriptStatus) {
        this.subscriptStatus = subscriptStatus;
    }

    public static AppInstance getInstance() {
        if (instance != null) {
            return instance;
        }
        synchronized (AppInstance.class) {
            if (instance != null) {
                return instance;
            }
            instance = new AppInstance();
            instance.setAppName(Config.Agent.SERVICE_NAME);
            instance.setAgentPort(Config.Agent.SERVER_PORT);
            instance.setVersion(Config.Agent.APP_VERSION);
            instance.setAgentVersion(Config.Agent.VERSION);
            instance.setEnv(Config.Agent.ENV);
            instance.setEnvStatus(Config.Agent.ENV_STATUS);
            if (Config.Agent.SUBSCRIPT_STATUS == -1) {
                instance.setSubscriptStatus(Config.Agent.ENV_STATUS);
            } else {
                instance.setSubscriptStatus(Config.Agent.SUBSCRIPT_STATUS);
            }
            instance.setIp(OSUtil.getIPV4());
            instance.setHostname(OSUtil.getHostName());
            instance.setUuid(SignatureUtils.createMd5(OSUtil.getIPV4() + ":" + System.currentTimeMillis()));
            instance.setStartAt(System.currentTimeMillis());
            return instance;
        }
    }

    public  static boolean isDoubleSubscript(){
        return instance.subscriptStatus==2;
    }

    public  static boolean isGraySubscript(){
        return instance.subscriptStatus==0;
    }

    public  static boolean isGrayStatus(){
        return instance.envStatus==0;
    }


}
