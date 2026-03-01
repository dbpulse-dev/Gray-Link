package com.dbcat.gray.agent.core;


import com.dbcat.gray.agent.core.boot.BootService;
import com.dbcat.gray.agent.core.conf.Config;
import com.dbcat.gray.agent.core.dto.InstanceTraffic;
import com.dbcat.gray.agent.core.logging.api.ILog;
import com.dbcat.gray.agent.core.logging.api.LogManager;
import com.dbcat.gray.agent.core.meter.Counter;
import com.dbcat.gray.agent.core.meter.CounterFactory;
import com.dbcat.gray.agent.core.meter.CounterManager;
import com.dbcat.gray.agent.core.server.RestResult;
import com.dbcat.gray.agent.util.HttpUtils;
import lombok.Getter;

import java.util.*;

/**
 * @author Blackfost
 */
@Getter
public class TrafficReportService implements BootService, Runnable {
    private static ILog log = LogManager.getLogger(TrafficReportService.class);


    @Override
    public void prepare() throws Throwable {

    }

    @Override
    public void boot() throws Throwable {
        new Thread(this, "instance-traffic").start();
    }

    @Override
    public void onComplete() throws Throwable {

    }

    @Override
    public void shutdown() throws Throwable {
    }

    @Override
    public int priority() {
        return Integer.MIN_VALUE;
    }

    @Override
    public void run() {
        while (true) {
            AppInstance instance = AppInstance.getInstance();
            Map<String, List<Counter>> metrics = buildMetrics();
            InstanceTraffic traffic = new InstanceTraffic();
            traffic.setAppName(instance.getAppName());
            traffic.setUuid(instance.getUuid());
            traffic.setMetrics(metrics);
            try {
                HttpUtils.request(Config.Agent.ADMIN_ADDR + Config.Agent.TRAFFIC_REPORT_URL, traffic, RestResult.class);
            } catch (Exception exception) {
                log.warn("上报服务异常:",exception);
            }
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private Map<String, List<Counter>> buildMetrics() {
        Map<String, CounterFactory> counterFactoryMap = CounterManager.getCounterFactoryMap();
        Map<String, List<Counter>> target = new HashMap<>();
        counterFactoryMap.forEach((name, factory) -> {
            List<Counter> counters = new ArrayList<>();
            target.put(name, counters);
            addCounter(factory.getNormalCounters(),counters);
            addCounter(factory.getGrayCounters(),counters);
        });
        return target;
    }

    private void addCounter(Map<String, Counter> countersMap,List<Counter> counters ){
        Set<Map.Entry<String, Counter>> entries = countersMap.entrySet();
        for(Map.Entry<String, Counter> entry:entries){
            Counter value = entry.getValue();
            if(value.getValue().intValue() ==0){
                continue;
            }
            counters.add(value);
        }
    }
}
