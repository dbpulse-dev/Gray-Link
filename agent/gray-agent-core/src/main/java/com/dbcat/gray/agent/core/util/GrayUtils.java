package com.dbcat.gray.agent.core.util;

import com.dbcat.gray.agent.core.AppInstance;

public abstract class GrayUtils {

    public static String rebuildName(String name) {
        AppInstance instance = AppInstance.getInstance();
        return rebuildName(name, instance.getEnvStatus());
    }

    public static String rebuildName(String name, int envStatus) {
        if (envStatus == 0) {
            if (name.endsWith("-gray")) {
                return name;
            }
            return name + "-gray";
        }
        if (name.endsWith("-gray")) {
            return name.replace("-gray", "");
        }
        return name;
    }
}
