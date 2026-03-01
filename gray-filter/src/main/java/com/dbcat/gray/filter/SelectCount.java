package com.dbcat.gray.filter;


import com.dbcat.gray.filter.constant.SelectorType;
import com.dbcat.gray.filter.dto.TrafficCount;

import java.util.ArrayList;
import java.util.List;

public interface SelectCount {
    /**
     * 总的请求量
     *
     * @return
     */
    long getTotalCount();

    /**
     * 灰度请求量
     *
     * @return
     */
    long getGrayCount();

    default List<TrafficCount> getCounts() {
        return new ArrayList<>(0);
    }

    default List<TrafficCount> getCounts(SelectorType type) {
        return new ArrayList<>(0);
    }

    void resetCounts();

    default void resetCounts(SelectorType type) {
    }

    ;

}
