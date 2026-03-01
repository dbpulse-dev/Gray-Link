package com.dbcat.gray.filter.selector;


import com.dbcat.gray.filter.Context;
import com.dbcat.gray.filter.SelectCount;
import com.dbcat.gray.filter.constant.SelectorType;
import com.dbcat.gray.filter.dto.GraySelectorStatus;

public interface Selector<CF, C extends Context> extends SelectCount {


    default boolean enable() {
        return true;
    }

    default void setEnable(boolean enable) {
    }

    ;

    void setConfig(CF config);

    void updateStatus(GraySelectorStatus status);

    SelectorType getType();

    Boolean selectEnv(C context);


}
