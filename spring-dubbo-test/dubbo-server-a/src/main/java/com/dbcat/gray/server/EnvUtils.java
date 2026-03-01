package com.dbcat.gray.server;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

public class EnvUtils {

    private static final Logger logger = LoggerFactory.getLogger(EnvUtils.class);


    public static String getXEnv() {
        try {
            Class<?> clazz = Class.forName("com.dbcat.gray.agent.core.util.ServerContextHolder");
            Method m = clazz.getMethod("getData", String.class);
            return (String) m.invoke(null, "x-env");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String removeEnv() {
        try {
            Class<?> clazz = Class.forName("com.dbcat.gray.agent.core.util.ServerContextHolder");
            Method m = clazz.getMethod("remove", String.class);
            return (String) m.invoke(null, "x-env");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String setEnv() {
        try {
            Class<?> clazz = Class.forName("com.dbcat.gray.agent.core.util.ServerContextHolder");
            Method m = clazz.getMethod("setData", String.class, Object.class);
            return (String) m.invoke(null, "x-env", "1");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getMessage() {
        if (getXEnv() != null) {
            return "灰度消息";
        } else {
            return "正常消息";
        }
    }

    public static void printEnv() {
        if (getXEnv() != null) {
            logger.info("灰度消息");
        } else {
            logger.info("正常消息");
        }
    }


}
