package com.dbcat.gray.agent.core.server;


import com.dbcat.gray.agent.core.conf.GrayConstant;
import com.dbcat.gray.agent.core.dto.Instance;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Blackfost
 */
public class ServerInstanceManager {

    private static final ScheduledThreadPoolExecutor checkExecutor = new ScheduledThreadPoolExecutor(2, runnable -> new Thread(runnable, "server-instance-status-check"), (runnable, threadPoolExecutor) -> runnable.run());

    private static Map<String/*serviceName*/, Map<String/*instance.uuid*/, Instance>> appInstances = new ConcurrentHashMap<>();

    private static Map<String, Instance> instanceMap = new ConcurrentHashMap<>();


    static {
        checkExecutor.scheduleAtFixedRate(new InstanceCheckExpireTask(appInstances, instanceMap), 30, 60, TimeUnit.SECONDS);
    }

    /**
     * {@link GrayConstant#ENV_STATUS_GRAY}
     * {@link GrayConstant#ENV_STATUS_NORMAL}
     *
     * @param serviceName
     * @param envStatus
     * @return
     */
    /**
     * @param serviceName
     * @param envStatus   {@link GrayConstant}
     * @return
     */
    public static Map<String, Instance> getInstances(String serviceName, int envStatus) {
        Map<String, Instance> instances = appInstances.get(serviceName);
        if (instances == null) {
            return Collections.EMPTY_MAP;
        }
        Map<String, Instance> targetMap = new HashMap<>();
        Set<Map.Entry<String, Instance>> entries = instances.entrySet();
        for (Map.Entry<String, Instance> entry : entries) {
            Instance value = entry.getValue();
            if (value.getEnvStatus() == envStatus) {
                targetMap.put(entry.getKey(), value);
            }
        }
        return targetMap;
    }

    public static void onInstanceChange(List<Instance> instances) {
        for (Instance request : instances) {
            onInstanceChange(request);
        }
    }

    public static synchronized void onInstanceChange(Instance change) {
        if (change.getAppName() == null) {
            return;
        }
        Map<String, Instance> instances = appInstances.get(change.getAppName());
        if (instances == null) {
            instances = new HashMap<>();
            appInstances.put(change.getAppName(), instances);
        }
        String uuid = change.getUuid();
        //实例已下线
        if (change.getStatus() == 2) {
            instances.remove(uuid);
            instanceMap.remove(uuid);
            return;
        }
        if (change.getStatus() == 0) {
            Instance instance = build(change);
            instances.put(uuid, instance);
            instanceMap.put(uuid, instance);
            return;
        }
        if (change.getStatus() == 1) {
            Instance instance = instances.get(uuid);
            if (instance == null) {
                instance = build(change);
                instances.put(uuid, instance);
                instanceMap.put(uuid, instance);
            } else {
                instance.setEnvStatus(change.getEnvStatus());
                instance.setExpireAt(change.getExpireAt());
                instance.setUpdateAt(new Date());
            }
            return;
        }

    }

    private static Instance build(Instance change) {
        Instance instance = new Instance();
        instance.setUuid(change.getUuid());
        instance.setAppName(change.getAppName());
        instance.setEnvStatus(change.getEnvStatus());
        instance.setUpdateAt(new Date());
        instance.setExpireAt(change.getExpireAt());
        return instance;
    }

    public static Instance getInstance(String uuid) {
        return instanceMap.get(uuid);
    }

}
