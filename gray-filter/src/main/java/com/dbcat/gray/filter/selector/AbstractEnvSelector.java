package com.dbcat.gray.filter.selector;


import com.dbcat.gray.filter.Context;
import com.dbcat.gray.filter.dto.GraySelectorStatus;

import java.util.concurrent.atomic.AtomicLong;

public abstract class AbstractEnvSelector<CF, C extends Context> implements Selector<CF, C> {

    protected AtomicLong totalCount = new AtomicLong(0);

    protected AtomicLong grayCount = new AtomicLong(0);

    private boolean enable;


    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    @Override
    public boolean enable() {
        return this.enable;
    }

    @Override
    public long getTotalCount() {
        return totalCount.longValue();
    }

    @Override
    public long getGrayCount() {
        return grayCount.longValue();
    }


    @Override
    public void updateStatus(GraySelectorStatus status) {
        this.enable = status.isEnable();
    }


    @Override
    public void resetCounts() {
        totalCount.set(0);
        grayCount.set(0);
    }
}
