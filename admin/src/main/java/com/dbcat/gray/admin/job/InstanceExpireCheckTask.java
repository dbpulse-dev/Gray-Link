package com.dbcat.gray.admin.job;

import com.dbcat.gray.admin.mapper.AppInstanceMapper;
import com.dbcat.gray.admin.mapper.InstanceTrafficMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
public class InstanceExpireCheckTask {

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    private final ScheduledThreadPoolExecutor connectUpdateExecutor = new ScheduledThreadPoolExecutor(1,
            runnable -> new Thread(null, runnable, "app-instance-expire-check", 0L),
            new ThreadPoolExecutor.AbortPolicy());

    @Autowired
    private AppInstanceMapper appInstanceMapper;

    @Autowired
    private InstanceTrafficMapper instanceTrafficMapper;

    @PostConstruct
    protected void startTask() {
        //定期更新信息
        connectUpdateExecutor.scheduleAtFixedRate(() -> {
            try {
                //清除3小时前的
                Date date = new Date(System.currentTimeMillis() - 180 * 60000);
                appInstanceMapper.deleteByReportAt(date);
                instanceTrafficMapper.deleteByReportAt(date);
            } catch (Exception e) {
                logger.warn("instance-expire-check 失败:{}", e.getMessage());
            }
        }, 20, 120, TimeUnit.SECONDS);
    }
}
