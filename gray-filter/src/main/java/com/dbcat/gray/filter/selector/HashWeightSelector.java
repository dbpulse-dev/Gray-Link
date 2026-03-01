package com.dbcat.gray.filter.selector;


import com.dbcat.gray.filter.Context;
import com.dbcat.gray.filter.constant.SelectorType;
import com.dbcat.gray.filter.dto.Pair;
import com.dbcat.gray.filter.dto.TrafficCount;
import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static com.dbcat.gray.filter.constant.SelectorType.HASH_WEIGHT;

/**
 * hash流量权重选择器
 */
public class HashWeightSelector extends AbstractEnvSelector<Integer, Context> implements WeightSelector {

    private volatile int grayWeight;


    @Override
    public Boolean selectEnv(Context request) {
        totalCount.incrementAndGet();
        List<Pair<String, String>> conditionValues = (List<Pair<String, String>>) request.getAttr("conditionValues");
        String requestValue = conditionValues.get(0).getValue();
        int hash = Hashing.murmur3_32_fixed()
                .hashString(requestValue, StandardCharsets.UTF_8)
                .asInt();
        boolean isGray = Math.abs(hash) % 100 < grayWeight;
        if (isGray) {
            grayCount.incrementAndGet();
        }
        return isGray;
    }

    public static HashWeightSelector build(int grayWeight) {
        HashWeightSelector handler = new HashWeightSelector();
        handler.setConfig(grayWeight);
        return handler;
    }

    @Override
    public void setConfig(Integer weight) {
        if (weight < 0) {
            throw new RuntimeException("权重不值不能小于0");
        }
        if (weight > 100) {
            throw new RuntimeException("权重不值不能大于100");
        }
        this.grayWeight = weight;
    }

    @Override
    public SelectorType getType() {
        return HASH_WEIGHT;
    }


    @Override
    public double getActualGrayRate() {
        long grayCount = getGrayCount();
        if (grayCount == 0) {
            return 0d;
        }
        return ((double) grayCount / getTotalCount()) * 100;
    }

    @Override
    public int getWeight() {
        return this.grayWeight;
    }

    @Override
    public List<TrafficCount> getCounts() {
        return Arrays.asList(TrafficCount.build(this));
    }
}
