package com.dbcat.gray.agent.core;


import com.dbcat.gray.agent.core.boot.BootService;
import com.dbcat.gray.agent.core.conf.Config;
import com.dbcat.gray.agent.core.logging.api.ILog;
import com.dbcat.gray.agent.core.logging.api.LogManager;
import com.dbcat.gray.agent.core.server.RestResult;
import com.dbcat.gray.agent.util.HttpUtils;
import com.dbcat.gray.agent.util.StringUtil;
import lombok.Getter;

/**
 * @author Blackfost
 */
@Getter
public class InstanceReportService implements BootService, Runnable {
    private static ILog log = LogManager.getLogger(InstanceReportService.class);

    private volatile boolean stop;

    @Override
    public void prepare() throws Throwable {
        if (!StringUtil.isEmpty(Config.Agent.INSTANCE_NAME)) {
            return;
        }
        AppInstance.getInstance();
    }

    @Override
    public void boot() throws Throwable {
        new Thread(this, "instance-report").start();
    }

    @Override
    public void onComplete() throws Throwable {

    }

    @Override
    public void shutdown() throws Throwable {
        this.stop = true;
        log.info("上报实例停上状态");
        AppInstance.getInstance().setStatus(2);
        try {
            HttpUtils.request(Config.Agent.ADMIN_ADDR + Config.Agent.REPORT_URL, AppInstance.getInstance(), RestResult.class);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public int priority() {
        return Integer.MIN_VALUE;
    }

    @Override
    public void run() {
        while (true) {
            reportInstance();
            try {
                Thread.sleep(60000);
            } catch (InterruptedException e) {
            }
            if (stop) {
                break;
            }
        }

    }

    public static void reportInstance() {
        final long l = System.currentTimeMillis();
        String uri = Config.Agent.ADMIN_ADDR + Config.Agent.REPORT_URL;
        try {
            RestResult result = HttpUtils.request(Config.Agent.ADMIN_ADDR + Config.Agent.REPORT_URL, AppInstance.getInstance(), RestResult.class);
            if (result.getCode() != 200) {
                log.warn("上报信息失改:{},{}", uri, result.getMessage());
                return;
            }
        } catch (Exception exception) {
            log.warn("上报实例信息异常:{}", exception.getMessage());
        } finally {
            log.info("实例信息上报耗时:{}", System.currentTimeMillis() - l);
        }

    }
}
