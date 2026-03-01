package com.dbcat.gray.agent.core.meter;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Blackfost
 */
public class Counter {
    private String name;

    /**
     * 0灰度，1正常
     */
    private int type;


    private AtomicLong value = new AtomicLong();

    public Counter(String name, int type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public AtomicLong getValue() {
        return value;
    }

    public void increase() {
        value.incrementAndGet();
    }

    public void increase(long count) {
        value.getAndAdd(count);
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
