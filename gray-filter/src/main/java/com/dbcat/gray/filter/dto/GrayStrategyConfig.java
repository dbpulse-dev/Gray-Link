package com.dbcat.gray.filter.dto;

import com.dbcat.gray.filter.constant.Operator;

import java.util.List;


public class GrayStrategyConfig {
    /**
     * 策略类型
     */
    private String type;

    private boolean enable;
    /**
     * 操作符
     * {@link Operator}
     */
    private List<Operator> operators;
    /**
     * 权重
     */
    private int weight;

    /**
     * 操作条件
     */
    private List<StrategyCondition> conditions;

    /**
     * 比较条件名(可选值,目前只有版本,如果有值则在存版本比较
     * 如果值为空,制全匹配
     */
    private String compareFieldName;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public List<Operator> getOperators() {
        return operators;
    }

    public void setOperators(List<Operator> operators) {
        this.operators = operators;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public List<StrategyCondition> getConditions() {
        return conditions;
    }

    public void setConditions(List<StrategyCondition> conditions) {
        this.conditions = conditions;
    }

    public String getCompareFieldName() {
        return compareFieldName;
    }

    public void setCompareFieldName(String compareFieldName) {
        this.compareFieldName = compareFieldName;
    }
}
