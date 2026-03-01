package com.dbcat.gray.agent.core;


import com.dbcat.gray.agent.core.boot.BootService;
import com.dbcat.gray.agent.core.conf.Config;
import com.dbcat.gray.agent.core.dto.Instance;
import com.dbcat.gray.agent.core.logging.api.ILog;
import com.dbcat.gray.agent.core.logging.api.LogManager;
import com.dbcat.gray.agent.core.server.RestResult;
import com.dbcat.gray.agent.core.server.ServerInstanceManager;
import com.dbcat.gray.agent.util.HttpUtils;
import com.google.common.reflect.TypeToken;
import lombok.Getter;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 获取当前应用的实例列表
 *
 * @author Blackfost
 */
@Getter
public class PullInstancesService implements BootService, Runnable {

    private static ILog log = LogManager.getLogger(PullInstancesService.class);

    private volatile boolean stop;

    @Override
    public void prepare() throws Throwable {

    }

    @Override
    public void boot() throws Throwable {
        new Thread(this, "pull-app-instances").start();
    }

    @Override
    public void onComplete() throws Throwable {

    }

    @Override
    public void shutdown() throws Throwable {
        this.stop = true;
    }

    @Override
    public int priority() {
        return Integer.MIN_VALUE + 1;
    }

    @Override
    public void run() {
        while (true) {
            pullInstances();
            try {
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (stop) {
                break;
            }
        }

    }


    private void pullInstances() {
        final long l = System.currentTimeMillis();
        try {
            Type type = new TypeToken<RestResult<List<Instance>>>() {
            }.getType();
            Map<String, String> request = new HashMap<>();
            request.put("appName", AppInstance.getInstance().getAppName());
            request.put("ip", AppInstance.getInstance().getIp());
            RestResult<List<Instance>> rs = HttpUtils.request(Config.Agent.ADMIN_ADDR + Config.Agent.APP_INSTANCES_PULL_URL, request, type);
            if (rs.getCode() != 200) {
                log.warn("拉取服务实例列表失败:{}", rs.getMessage());
                return;
            }
            List<Instance> instances = rs.getData();
            log.info("拉取服务实例列表,数量:{}", instances.size());
            for (Instance instance : instances) {
                ServerInstanceManager.onInstanceChange(instance);
            }
        } catch (Throwable e) {
            //拉取失败
            log.error("拉取服务实例列表失败:", e);
        } finally {
            log.info("拉取服务实例列表耗时:{}", System.currentTimeMillis() - l);
        }
    }
}
