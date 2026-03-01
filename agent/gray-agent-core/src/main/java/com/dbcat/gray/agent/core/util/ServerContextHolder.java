package com.dbcat.gray.agent.core.util;


import java.util.HashMap;
import java.util.Map;


public class ServerContextHolder {

    private final static ThreadLocal<Map<String, Object>> contextHolder = ThreadLocal.withInitial(HashMap::new);

    public static void set(String key, Object value) {
        if (value == null) {
            contextHolder.get().remove(key);
        } else {
            contextHolder.get().put(key, value);
        }
    }

    public static void remove(String key) {
        contextHolder.get().remove(key);
    }

    public static Object get(String key) {
        return contextHolder.get().get(key);
    }


    public static void clear() {
        contextHolder.remove();
    }


}
