package com.dbcat.gray.filter.dto;


import com.dbcat.gray.filter.constant.SelectorType;

public class GraySelectorStatus {

    private SelectorType type;

    private boolean enable;

    public SelectorType getType() {
        return type;
    }

    public void setType(SelectorType type) {
        this.type = type;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}
