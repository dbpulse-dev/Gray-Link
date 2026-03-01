package com.dbcat.gray.admin.dto;


public class GrayConditionAdd {

    /**
     * 类型:user，client,domain,tenant
     */
    private String type;

    /**
     * 灰度条件值
     */
    private String value;


    private Integer weight;

    private String operators;

    private Integer enable;

    public Integer getEnable() {
        return enable;
    }

    public void setEnable(Integer enable) {
        this.enable = enable;
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
