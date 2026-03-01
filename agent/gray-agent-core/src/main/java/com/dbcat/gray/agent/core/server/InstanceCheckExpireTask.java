package com.dbcat.gray.agent.core.server;

import com.dbcat.gray.agent.core.dto.Instance;
import com.dbcat.gray.agent.core.logging.api.ILog;
import com.dbcat.gray.agent.core.logging.api.LogManager;

import java.util.Iterator;
import java.util.Map;

/**
 * @author Blackfost
 */
public class InstanceCheckExpireTask implements Runnable {

    private static final ILog logger = LogManager.getLogger(InstanceCheckExpireTask.class);


    private Map<String/*serviceName*/, Map<String, Instance>> serviceInstances;

    private Map<String/*uuid*/, Instance> instanceMap;

    public InstanceCheckExpireTask(Map<String, Map<String, Instance>> serviceInstances, Map<String, Instance> instanceMap) {
        this.serviceInstances = serviceInstances;
        this.instanceMap = instanceMap;
    }

    @Override
    public void run() {
        try {
            serviceInstances.forEach((appName, instanceMap) -> {
                Iterator<Map.Entry<String, Instance>> iterator = instanceMap.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, Instance> entry = iterator.next();
                    Instance instance = entry.getValue();
                    if (instance.getExpireAt() < System.currentTimeMillis()) {
                        logger.info("实例太久没更新，认为已无效下线{}", instance.getUuid());
                        iterator.remove();
                    }
                }
            });
        } catch (Throwable t) {
            logger.error("探测实例状态异常", t);
        }
        try {
            Iterator<Map.Entry<String, Instance>> iterator = instanceMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Instance> entry = iterator.next();
                Instance instance = entry.getValue();
                if (instance.getExpireAt() < System.currentTimeMillis()) {
                    logger.info("实例太久没更新，认为已无效下线{}", instance.getUuid());
                    iterator.remove();
                }
            }
        } catch (Throwable t) {
            logger.error("探测实例状态异常", t);
        }
    }
}
