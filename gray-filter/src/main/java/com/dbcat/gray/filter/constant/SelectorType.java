package com.dbcat.gray.filter.constant;

public enum SelectorType {
    CONDITION("条件选择器"),
    HASH_WEIGHT("hash权重"),
    SIMPLE_WEIGHT("简单权重");
    private String description;

    SelectorType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
