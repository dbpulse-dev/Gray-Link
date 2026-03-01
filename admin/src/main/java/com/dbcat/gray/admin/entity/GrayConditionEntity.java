package com.dbcat.gray.admin.entity;


public class GrayConditionEntity {

    private Integer id;

    /**
     * 类型:user，client,domain,tenant
     */
    private String type;

    /**
     * 灰度条件值
     */
    private String value;


    /**
     * 访问量统计
     */
    private long accessTotalCount;

    /**
     * 灰度访问量统计
     */
    private long accessGrayCount;

    private Integer weight;

    private String operators;

    private Integer enable;

    public Integer getEnable() {
        return enable;
    }

    public void setEnable(Integer enable) {
        this.enable = enable;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public long getAccessTotalCount() {
        return accessTotalCount;
    }

    public void setAccessTotalCount(long accessTotalCount) {
        this.accessTotalCount = accessTotalCount;
    }

    public long getAccessGrayCount() {
        return accessGrayCount;
    }

    public void setAccessGrayCount(long accessGrayCount) {
        this.accessGrayCount = accessGrayCount;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public String getOperators() {
        return operators;
    }

    public void setOperators(String operators) {
        this.operators = operators;
    }
}
