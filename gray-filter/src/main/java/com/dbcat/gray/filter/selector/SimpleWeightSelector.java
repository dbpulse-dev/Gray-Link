package com.dbcat.gray.filter.selector;


import com.dbcat.gray.filter.Context;
import com.dbcat.gray.filter.constant.SelectorType;
import com.dbcat.gray.filter.dto.TrafficCount;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 流量权重选择器
 */
public class SimpleWeightSelector extends AbstractEnvSelector<Integer, Context> implements WeightSelector {

    private volatile CircularLinkedListQueue<Boolean> weightQueue = new CircularLinkedListQueue<>();

    private volatile int grayWeight;

    private volatile boolean initQueue;


    @Override
    public Boolean selectEnv(Context request) {
        totalCount.incrementAndGet();
        if (!initQueue) {
            return null;
        }
        Boolean next = weightQueue.getNext();
        if (next) {
            grayCount.incrementAndGet();
        }
        return next;
    }

    public static SimpleWeightSelector build(int grayWeight) {
        SimpleWeightSelector handler = new SimpleWeightSelector();
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
        this.weightQueue = buildWeightQueue();
        this.initQueue = true;
    }

    @Override
    public SelectorType getType() {
        return SelectorType.SIMPLE_WEIGHT;
    }


    private CircularLinkedListQueue<Boolean> buildWeightQueue() {
        CircularLinkedListQueue<Boolean> queue = new CircularLinkedListQueue<>();
        if (grayWeight == 100) {
            queue.enqueue(true);
            queue.enqueue(true);
            return queue;
        }
        List<Boolean> data = Stream.concat(Collections.nCopies(grayWeight, true).stream(),
                Collections.nCopies(100 - grayWeight, false).stream()
        ).collect(Collectors.toList());
        Collections.shuffle(data);
        data.forEach(d -> {
            queue.enqueue(d);
        });
        return queue;
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
