package com.dbcat.gray.admin.entity;


public class InstanceTrafficEntity {

    private Integer id;

    private String appName;

    private String uuid;

    /**
     * produce,consume
     */
    private String direction;

    private Integer type;

    /**
     * 灰度条件值
     */
    private String name;

    /**
     * 访问量统计
     */
    private long value;

    public String getDirection() {
        return direction;
    }

    public Integer getType() {
        return type;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public void setType(Integer type) {
        this.type = type;
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }


}
