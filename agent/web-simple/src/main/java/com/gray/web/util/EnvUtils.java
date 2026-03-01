package com.gray.web.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

public class EnvUtils {

    private static final Logger logger = LoggerFactory.getLogger(EnvUtils.class);


    public static String getXEnv() {
        try {
            Class<?> clazz = Class.forName("com.dbcat.gray.agent.core.util.ServerContextHolder");
            Method m = clazz.getMethod("get", String.class);
            return (String) m.invoke(null, "x_env");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String removeEnv() {
        try {
            Class<?> clazz = Class.forName("com.dbcat.gray.agent.core.util.ServerContextHolder");
            Method m = clazz.getMethod("remove", String.class);
            return (String) m.invoke(null, "x_env");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String setEnv() {
        try {
            Class<?> clazz = Class.forName("com.dbcat.gray.agent.core.util.ServerContextHolder");
            Method m = clazz.getMethod("setData", String.class, Object.class);
            return (String) m.invoke(null, "x_env", "1");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getMessage(String topic) {
        if (getXEnv() != null) {
            return topic + ":灰度请求";
        } else {
            return topic + ":正常请求";
        }
    }

    public static void printEnv() {
        if (getXEnv() != null) {
            logger.info("灰度请求");
        } else {
            logger.info("正常请求");
        }
    }


}
