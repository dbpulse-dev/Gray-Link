package com.dbcat.gray.admin.dto;

import java.util.List;
import java.util.Map;

public class InstanceTraffic {

    private String uuid;

    private String appName;

    Map<String, List<Counter>> metrics;


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

    public Map<String, List<Counter>> getMetrics() {
        return metrics;
    }

    public void setMetrics(Map<String, List<Counter>> metrics) {
        this.metrics = metrics;
    }
}
