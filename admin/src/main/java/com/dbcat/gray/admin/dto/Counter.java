package com.dbcat.gray.admin.dto;

public class Counter {

    private String name;

    /**
     * 0灰度，1正常
     */
    private int type;

    private long value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }
}
