package com.dbcat.gray.agent.core.meter;

import com.dbcat.gray.agent.core.dto.ComponentType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Blackfost
 */
public class CounterFactory {

    private String name;

    public CounterFactory(String name) {
        this.name = name;
    }

    private Map<String, Counter> normalCounters = new ConcurrentHashMap<>();

    private Map<String, Counter> grayCounters = new ConcurrentHashMap<>();

    public String getName() {
        return name;
    }


    public void increase(ComponentType componentType, Object env, long count) {
        Map<String, Counter> counterMap = env == null ? normalCounters : grayCounters;
        Counter counter = counterMap.get(componentType.getType());
        counter.increase(count);
    }

    public void addCounter(String name, int type) {
        Counter counter = new Counter(name, type);
        if (type == 0) {
            grayCounters.put(name, counter);
        } else {
            normalCounters.put(name, counter);
        }
    }

    public Map<String, Counter> getNormalCounters() {
        return normalCounters;
    }

    public Map<String, Counter> getGrayCounters() {
        return grayCounters;
    }
}
