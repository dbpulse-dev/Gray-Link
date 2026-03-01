package com.dbcat.gray.admin.service.impl;

import com.dbcat.gray.admin.constants.EnvStatus;
import com.dbcat.gray.admin.constants.SubscriptStatus;
import com.dbcat.gray.admin.dto.*;
import com.dbcat.gray.admin.entity.AppInstanceEntity;
import com.dbcat.gray.admin.event.InstanceReportEvent;
import com.dbcat.gray.admin.mapper.AppInstanceMapper;
import com.dbcat.gray.admin.service.AppInstanceService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class AppInstanceServiceImpl implements AppInstanceService {

    @Autowired
    private AppInstanceMapper appInstanceMapper;

    final static long EXPIRE_TIME = 60000;

    @Autowired
    private ApplicationContext applicationContext;

    @Value("${gray.subscript.enable:true}")
    private boolean subscriptEnable;


    @Override
    public synchronized void onInstanceReport(AppInstanceReport report) {
        InstanceReportEvent reportEvent = new InstanceReportEvent(report);
        applicationContext.publishEvent(reportEvent);
        AppInstanceResponse response = appInstanceMapper.getByUuid(report.getUuid());
        AppInstanceEntity entity = new AppInstanceEntity();
        BeanUtils.copyProperties(report, entity);
        entity.setStartAt(new Date(report.getStartAt()));
        if (response == null) {
            appInstanceMapper.add(entity);
            return;
        }
        entity.setId(response.getId());
        appInstanceMapper.updateByReport(entity);
    }

    @Override
    public Page<AppInstanceResponse> listByPage(AppInstancePageRequest request) {
        Page<AppInstanceResponse> page = PageHelper.startPage(request.getPageNum(), request.getPageSize());
        appInstanceMapper.list(request);
        return page;
    }


    @Override
    public List<InstanceStatus> getInstances(InstancesRequest request) {
        AppInstanceRequest instanceRequest = new AppInstanceRequest();
        List<AppInstanceResponse> list = appInstanceMapper.list(instanceRequest);
        return list.stream().filter(n -> n.getStatus() == 1).map(n -> {
            InstanceStatus instance = new InstanceStatus();
            instance.setAppName(n.getAppName());
            instance.setEnvStatus(n.getEnvStatus());
            instance.setStatus(n.getStatus());
            instance.setUuid(n.getUuid());
            instance.setExpireAt(System.currentTimeMillis() + EXPIRE_TIME * 5);
            return instance;
        }).collect(Collectors.toList());
    }

    @Override
    public InstanceSubscriptStatus getSubscriptStatus(SubscriptStatusRequest request) {
        List<AppInstanceResponse> responseList = appInstanceMapper.listStatusByName(request.getAppName());
        int grays = 0;
        int normals = 0;
        for (AppInstanceResponse instance : responseList) {
            if (instance.getStatus() > 1) {
                continue;
            }
            if (instance.getEnvStatus() == 0) {
                grays++;
            }
            if (instance.getEnvStatus() == 1) {
                normals++;
            }
        }
        InstanceSubscriptStatus instanceSubscriptStatus = new InstanceSubscriptStatus();
        instanceSubscriptStatus.setEnable(subscriptEnable ? 1 : 0);
        int status = buildScriptStatus(request.getEnvStatus(), normals, grays);
        instanceSubscriptStatus.setSubscriptStatus(status);
        return instanceSubscriptStatus;
    }


    private static int buildScriptStatus(int envStatus, int normals, int grays) {
        if (envStatus == EnvStatus.GRAY.getCode() && normals > 0) {
            return SubscriptStatus.GRAY.getStatus();
        }
        if (envStatus == EnvStatus.GRAY.getCode() && normals < 1) {
            return SubscriptStatus.DOUBLE.getStatus();
        }
        if (envStatus != EnvStatus.IDLE.getCode() && grays < 1) {
            return SubscriptStatus.DOUBLE.getStatus();
        }
        return SubscriptStatus.NORMAL.getStatus();
    }


}
