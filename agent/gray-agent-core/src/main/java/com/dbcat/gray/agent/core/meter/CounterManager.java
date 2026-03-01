package com.dbcat.gray.agent.core.meter;

import com.dbcat.gray.agent.core.dto.ComponentType;

import java.util.HashMap;
import java.util.Map;

public class CounterManager {

    private static Map<String, CounterFactory> counterFactoryMap = new HashMap<>();

    private static final String PRODUCE_NAME = "produce";
    private static final String CONSUME_NAME = "consume";

    static {
        CounterFactory publish = new CounterFactory(PRODUCE_NAME);
        CounterFactory consume = new CounterFactory(CONSUME_NAME);
        counterFactoryMap.put(PRODUCE_NAME, publish);
        counterFactoryMap.put(CONSUME_NAME, consume);
        counterFactoryMap.forEach((k, f) -> initCounterFactory(f));
    }

    private static void initCounterFactory(CounterFactory factory) {
        for (ComponentType componentType : ComponentType.values()) {
            factory.addCounter(componentType.getType(), 0);
            factory.addCounter(componentType.getType(), 1);
        }
    }

    public static void increasePublish(ComponentType type, Object env) {
        increasePublish(type, env, 1);
    }

    public static void increasePublish(ComponentType type, Object env, long count) {
        increase(type, env, count, PRODUCE_NAME);
    }

    public static void increaseConsume(ComponentType type, Object env) {
        increaseConsume(type, env, 1);
    }

    public static void increaseConsume(ComponentType counterName, Object env, long count) {
        increase(counterName, env, count, CONSUME_NAME);
    }

    private static void increase(ComponentType type, Object env, long count, String factoryName) {
        CounterFactory factory = counterFactoryMap.get(factoryName);
        factory.increase(type, env, count);
    }

    public static Map<String, CounterFactory> getCounterFactoryMap() {
        return counterFactoryMap;
    }
}
