package com.dbcat.gray.admin.service.impl;

import com.dbcat.gray.admin.dto.AppInstanceResponse;
import com.dbcat.gray.admin.dto.NotifyInstanceChangeRequest;
import com.dbcat.gray.admin.dto.RestResult;
import com.dbcat.gray.admin.utils.InstanceInvokerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.Callable;

public class NotifyChangeTask implements Callable<RestResult>, Runnable {

    protected static final Logger logger = LoggerFactory.getLogger(NotifyChangeTask.class);

    private List<NotifyInstanceChangeRequest> requests;

    private AppInstanceResponse node;

    public NotifyChangeTask(AppInstanceResponse node, List<NotifyInstanceChangeRequest> requests) {
        this.node = node;
        this.requests = requests;
    }

    @Override
    public RestResult call() {
        try {
            return InstanceInvokerHelper.invokeRemote(node, requests, "/gray/instance/change", "通知实例状态变化", 2);
        } catch (Throwable t) {
            logger.error("通知实例发生变化异常:{},{},", node.getAppName(), node.getIp(), t);
            return RestResult.buildFailure(t.getMessage());
        }
    }

    @Override
    public void run() {
        this.call();
    }
}