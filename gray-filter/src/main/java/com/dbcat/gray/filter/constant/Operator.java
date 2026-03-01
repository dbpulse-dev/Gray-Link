package com.dbcat.gray.filter.constant;

public enum Operator {
    EQ("等于"),
    GT("大于"),
    LT("小于");
    private String description;

    Operator(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
