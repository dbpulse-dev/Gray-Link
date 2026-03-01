package com.dbcat.gray.filter.dto;

import com.dbcat.gray.filter.selector.Selector;

public class TrafficCount {

    private String name;
    private String type;
    private long totalCount;
    private long grayCount;
    private boolean enable;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public long getGrayCount() {
        return grayCount;
    }

    public void setGrayCount(long grayCount) {
        this.grayCount = grayCount;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public static TrafficCount build(Selector selectCount) {
        TrafficCount trafficCount = new TrafficCount();
        trafficCount.setTotalCount(selectCount.getTotalCount());
        trafficCount.setGrayCount(selectCount.getGrayCount());
        trafficCount.setEnable(selectCount.enable());
        return trafficCount;


    }
}
