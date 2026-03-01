package com.dbcat.gray.admin.service.impl;


import com.dbcat.gray.admin.dto.*;
import com.dbcat.gray.admin.event.InstanceReportEvent;
import com.dbcat.gray.admin.mapper.AppInstanceMapper;
import com.dbcat.gray.admin.service.GrayControlService;
import com.dbcat.gray.admin.utils.InstanceInvokerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class GrayControlServiceImpl implements GrayControlService, ApplicationListener<InstanceReportEvent> {

    protected static final Logger logger = LoggerFactory.getLogger(GrayControlServiceImpl.class);

    long EXPIRE_TIME = 60000;

    private static ThreadPoolExecutor instanceNotifyExecutor;

    @Autowired
    private AppInstanceMapper appInstanceMapper;

    @PostConstruct
    public void init() {
        instanceNotifyExecutor = new ThreadPoolExecutor(20, 100, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(3000),
                new ThreadFactory() {
                    private int index;

                    @Override
                    public Thread newThread(Runnable runnable) {
                        index++;
                        return new Thread(runnable, "instance-notify-" + index);
                    }
                },
                (runnable, threadPoolExecutor) -> {
                    logger.info("灰度通知池满了..");
                    runnable.run();
                });
    }

    @Override
    public void onApplicationEvent(InstanceReportEvent event) {
        AppInstanceReport data = event.getData();
        if (data.getStatus() == 1) {
            return;
        }
        String uuid = data.getUuid();
        AppInstanceResponse instanceResponse = appInstanceMapper.getByUuid(uuid);
        instanceResponse.setEnvStatus(data.getEnvStatus());
        instanceResponse.setStatus(data.getStatus());
        doNotifyInstances(Arrays.asList(instanceResponse));
    }

    @Override
    public void updateInstanceStatus(InstanceStatusUpdate update) {
        AppInstanceResponse instance = appInstanceMapper.getById(update.getId());
        if (instance == null) {
            throw new RuntimeException("实例不存在");
        }
        instance.setEnvStatus(update.getEnvStatus());
        List<AppInstanceResponse> changeInstances = Arrays.asList(instance);
        updateInstancesEnvStatus(changeInstances);
        doNotifyInstances(changeInstances);
    }

    @Override
    public List<SubscriptResponse> updateSubscriptStatus(InstanceSubscriptStatusUpdate update) {
        AppInstanceResponse instance = appInstanceMapper.getById(update.getId());
        if (instance == null) {
            throw new RuntimeException("实例不存在");
        }
        RestResult<List<SubscriptResponse>> rs = InstanceInvokerHelper.invokeRemote(instance, update, "/subscript/update", "设置灰度状态", 2, new ParameterizedTypeReference<RestResult<List<SubscriptResponse>>>() {
            @Override
            public Type getType() {
                return super.getType();
            }
        });
        return rs.getData();
    }

    @Override
    public void updateAppStatus(AppStatusUpdate update) {
        AppInstanceRequest request = new AppInstanceRequest();
        request.setAppName(update.getAppName());
        List<AppInstanceResponse> changeInstances = appInstanceMapper.list(request);
        changeInstances.forEach(i -> i.setEnvStatus(update.getEnvStatus()));
        updateInstancesEnvStatus(changeInstances);
        doNotifyInstances(changeInstances);
    }

    @Override
    public void batchUpdateAppStatus(AppStatusBatchUpdate updateList) {
        List<AppInstanceResponse> changeInstances = appInstanceMapper.listByNames(updateList.getAppNames());
        updateInstancesEnvStatus(changeInstances);
        doNotifyInstances(changeInstances);
    }

    private void doNotifyInstances(List<AppInstanceResponse> changeInstances) {
        List<AppInstanceResponse> notifyInstances = this.getNotifyInstances();
        Map<String, AppInstanceResponse> changeMap = changeInstances.stream().filter(n -> n.getStatus() > 1).collect(Collectors.toMap(AppInstanceResponse::getUuid, Function.identity(), (key1, key2) -> key2));
        notifyInstances = notifyInstances.stream().filter(n -> !changeMap.containsKey(n.getUuid())).collect(Collectors.toList());
        logger.info("异步通知所有实例，有实例发生变化:{}", notifyInstances.size());
        List<NotifyInstanceChangeRequest> requests = buildNotifyRequests(changeInstances);
        for (AppInstanceResponse node : notifyInstances) {
            instanceNotifyExecutor.execute(new NotifyChangeTask(node, requests));
        }
    }

    private void updateInstancesEnvStatus(List<AppInstanceResponse> changeInstances) {
        List<AppInstanceResponse> instances = changeInstances.stream().filter(i -> i.getStatus() < 2).collect(Collectors.toList());
        logger.info("更新实例env-status:{}", instances.size());
        for (AppInstanceResponse node : instances) {
            AppStatusUpdate update = new AppStatusUpdate();
            update.setAppName(node.getAppName());
            update.setEnvStatus(node.getEnvStatus());
            instanceNotifyExecutor.execute(() -> InstanceInvokerHelper.invokeRemote(node, update, "/env-status/update", "设置灰度状态", 2));
        }
    }

    private List<AppInstanceResponse> getNotifyInstances() {
        AppInstanceRequest allRequest = new AppInstanceRequest();
        List<AppInstanceResponse> notifyInstances = appInstanceMapper.list(allRequest);
        return notifyInstances.stream().filter(i -> i.getStatus() < 2).collect(Collectors.toList());
    }


    private List<NotifyInstanceChangeRequest> buildNotifyRequests(List<AppInstanceResponse> nodes) {
        return nodes.stream().map(changeInstance -> {
            NotifyInstanceChangeRequest request = new NotifyInstanceChangeRequest();
            //更新的状态
            request.setEnvStatus(changeInstance.getEnvStatus());
            request.setAppName(changeInstance.getAppName());
            request.setIp(changeInstance.getIp());
            request.setUuid(changeInstance.getUuid());
            request.setStatus(changeInstance.getStatus());
            //在客户端设计有效期，如果下线没更新自动删
            request.setExpireAt(System.currentTimeMillis() + EXPIRE_TIME * 30);
            return request;
        }).collect(Collectors.toList());
    }
}
