package com.dbcat.gray.agent.core;


import com.dbcat.gray.agent.core.boot.BootService;
import com.dbcat.gray.agent.core.conf.Config;
import com.dbcat.gray.agent.core.dto.SubscriptUpdate;
import com.dbcat.gray.agent.core.logging.api.ILog;
import com.dbcat.gray.agent.core.logging.api.LogManager;
import com.dbcat.gray.agent.core.mq.MQConnectionManager;
import com.dbcat.gray.agent.core.server.RestResult;
import com.dbcat.gray.agent.util.HttpUtils;
import com.google.common.reflect.TypeToken;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 获取当前实例应设的订阅状态
 *
 * @author Blackfost
 */
@Getter
public class GetSubscriptStatusService implements BootService, Runnable {

    private static ILog log = LogManager.getLogger(GetSubscriptStatusService.class);

    private volatile boolean stop;

    @Override
    public void prepare() throws Throwable {

    }

    @Override
    public void boot() throws Throwable {
        new Thread(this, "get-app-status").start();
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
            try {
                SubscriptUpdate subscriptStatus = getSubscriptStatus();
                updateSubscriptStatus(subscriptStatus);
                Thread.sleep(20000);
            } catch (Exception e) {
                log.error("获取实例订阅状态失败:", e);
            }
            if (stop) {
                break;
            }
        }

    }


    private SubscriptUpdate getSubscriptStatus() throws Exception {
        AppInstance instance = AppInstance.getInstance();
        Map<String, String> request = new HashMap<>();
        request.put("appName", instance.getAppName());
        request.put("ip", instance.getIp());
        request.put("envStatus", instance.getEnvStatus() + "");
        request.put("appVersion", instance.getVersion());
        request.put("uuid", instance.getUuid());
        request.put("env", instance.getEnv());
        RestResult<SubscriptUpdate> rs = HttpUtils.request(Config.Agent.ADMIN_ADDR + Config.Agent.APP_INSTANCES_SUBSCRIPT_STATUS_URL, request, new TypeToken<RestResult<SubscriptUpdate>>() {
        }.getType());
        if (rs.getCode() != 200) {
            log.warn("获取实例状态统计失败:{}", rs.getMessage());
            return null;
        }
        return rs.getData();
    }

    private void updateSubscriptStatus(SubscriptUpdate update) {
        if (update.getEnable() == 0) {
            return;
        }
        AppInstance instance = AppInstance.getInstance();
        boolean notChange = instance.getSubscriptStatus() == update.getSubscriptStatus();
        if (notChange) {
            return;
        }
        instance.setSubscriptStatus(update.getSubscriptStatus());
        InstanceReportService.reportInstance();
        log.info("订阅状态发生变化,重新调整的订阅:{}", update.getSubscriptStatus());
        try {
            MQConnectionManager.onSubscriptChange(3);
        } catch (Exception exception) {
            log.error("重新调整的订阅异常:", exception);
        }
    }
}
