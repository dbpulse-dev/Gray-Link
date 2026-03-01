package com.dbcat.gray.filter.selector;

import com.dbcat.gray.filter.Context;
import com.dbcat.gray.filter.VersionComparator;
import com.dbcat.gray.filter.constant.Operator;
import com.dbcat.gray.filter.constant.SelectorType;
import com.dbcat.gray.filter.dto.GraySelectorStatus;
import com.dbcat.gray.filter.dto.GrayStrategyConfig;
import com.dbcat.gray.filter.dto.GrayValue;

import java.util.List;

public class ConditionSelector implements Selector<Integer, Context> {
    private GrayValue condition;
    private AbstractEnvSelector selector;

    public ConditionSelector(AbstractEnvSelector selector) {
        this.selector = selector;
    }

    public GrayValue getCondition() {
        return condition;
    }

    public void setCondition(GrayValue condition) {
        this.condition = condition;
    }

    public AbstractEnvSelector getSelector() {
        return selector;
    }

    public void setSelector(AbstractEnvSelector selector) {
        this.selector = selector;
    }

    @Override
    public void setConfig(Integer config) {
        selector.setConfig(config);
    }

    @Override
    public void updateStatus(GraySelectorStatus status) {
        selector.updateStatus(status);
    }

    @Override
    public SelectorType getType() {
        return selector.getType();
    }

    public Boolean selectEnv(Context context, GrayStrategyConfig strategyConfig) {
        String targetValue = this.condition.getValue();
        List<Operator> ops = condition.getOperators();
        if (ops == null) {
            ops = strategyConfig.getOperators();
        }
        if (ops == null) {
            return null;
        }
        String requestValue = (String) context.getAttr("requestValue");
        boolean match = VersionComparator.match(ops, requestValue, targetValue);
        if (match) {
            return this.selectEnv(context);
        }
        return null;
    }

    @Override
    public Boolean selectEnv(Context request) {
        return this.selector.selectEnv(request);
    }

    @Override
    public long getTotalCount() {
        return selector.getTotalCount();
    }

    @Override
    public long getGrayCount() {
        return selector.getGrayCount();
    }

    @Override
    public void resetCounts() {
        selector.resetCounts();
    }
}
