package com.dbcat.gray.filter;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

public class RouteLabelUtils {

    private static final Logger logger = LoggerFactory.getLogger(RouteLabelUtils.class);

    public final static String ROUTE_LABEL = "x_env";

    private final static String CONTEXT_HOLDER_CLASS = "com.dbcat.gray.agent.core.util.ServerContextHolder";


    public static void remove() {
        try {
            Class<?> clazz = Class.forName(CONTEXT_HOLDER_CLASS);
            Method m = clazz.getMethod("remove", String.class);
            m.invoke(null, ROUTE_LABEL);
        } catch (Exception e) {
            logger.warn("移除环境标签失败:{}", e.toString());
        }
    }

    public static void set() {
        try {
            Class<?> clazz = Class.forName(CONTEXT_HOLDER_CLASS);
            Method m = clazz.getMethod("set", String.class, Object.class);
            m.invoke(null, ROUTE_LABEL, "1");
        } catch (Exception e) {
            logger.warn("添加环境标签失败:{}", e.toString());
        }
    }

}
