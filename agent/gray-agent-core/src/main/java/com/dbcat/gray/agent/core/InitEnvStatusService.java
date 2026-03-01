package com.dbcat.gray.agent.core;


import com.dbcat.gray.agent.core.boot.BootService;
import com.dbcat.gray.agent.core.conf.Config;
import com.dbcat.gray.agent.core.dto.ServerDeployEnv;
import com.dbcat.gray.agent.core.logging.api.ILog;
import com.dbcat.gray.agent.core.logging.api.LogManager;
import com.dbcat.gray.agent.core.server.RestResult;
import com.dbcat.gray.agent.util.HttpUtils;
import com.google.common.reflect.TypeToken;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 当前实例的初始化状态
 *
 * @author Blackfost
 */
@Getter
public class InitEnvStatusService implements BootService, Runnable {

    private static ILog log = LogManager.getLogger(InitEnvStatusService.class);

    private volatile boolean stop;

    @Override
    public void prepare() throws Throwable {

    }

    @Override
    public void boot() throws Throwable {
        new Thread(this, "app-init-env-status").start();
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
        getAndInitEnvStatus(2);
    }


    private void getAndInitEnvStatus(int retry) {
        if (retry < 1) {
            return;
        }
        log.info("获取服务部署环境状态并初始化");
        retry--;
        final long l = System.currentTimeMillis();
        try {
            AppInstance instance = AppInstance.getInstance();
            Map<String, String> request = new HashMap<>();
            request.put("appName", instance.getAppName());
            request.put("ip", instance.getIp());
            request.put("appVersion", instance.getVersion());
            request.put("uuid", instance.getUuid());
            request.put("env", instance.getEnv());
            RestResult<ServerDeployEnv> rs = HttpUtils.request(Config.Agent.ADMIN_ADDR + Config.Agent.APP_DEPLOY_ENV_STATUS_URL, request, new TypeToken<RestResult<ServerDeployEnv>>() {
            }.getType());
            if (rs.getCode() != 200) {
                log.warn("获取取服务部署环境状态失败:{}", rs.getMessage());
                return;
            }
            ServerDeployEnv data = rs.getData();
            if (data.getEnable() == 0) {
                return;
            }
            instance.setEnvStatus(data.getEnvStatus());
        } catch (Throwable e) {
            log.error("获取取服务部署环境状态失败:", e);
            try {
                Thread.sleep(1000);
            } catch (Exception ex) {
            }
            getAndInitEnvStatus(retry);
        }
    }
}
