package com.dbcat.gray.agent.core.server.endpoint;


import com.dbcat.gray.agent.core.AppInstance;
import com.dbcat.gray.agent.core.InstanceReportService;
import com.dbcat.gray.agent.core.dto.ExecuteResponse;
import com.dbcat.gray.agent.core.dto.SubscriptUpdate;
import com.dbcat.gray.agent.core.logging.api.ILog;
import com.dbcat.gray.agent.core.logging.api.LogManager;
import com.dbcat.gray.agent.core.mq.MQConnectionManager;
import com.dbcat.gray.agent.core.server.RestResult;

import java.util.List;

/**
 * 更新订阅状态
 *
 * @author Blackfost
 */
public class SubscriptUpdateEndpoint implements Endpoint<SubscriptUpdate, List<ExecuteResponse>> {

    private static final ILog log = LogManager.getLogger(SubscriptUpdateEndpoint.class);

    @Override
    public String path() {
        return "/subscript/update";
    }

    @Override
    public RestResult<List<ExecuteResponse>> invoke(SubscriptUpdate request) throws Exception {
        log.info("更新实例订阅状态:{}", request.getSubscriptStatus());
        AppInstance instance = AppInstance.getInstance();
        int subscriptStatus = instance.getSubscriptStatus();
        if (request.getSubscriptStatus() == subscriptStatus) {
            return RestResult.buildSuccess();
        }
        instance.setSubscriptStatus(request.getSubscriptStatus());
        InstanceReportService.reportInstance();
        log.info("订阅状态发生变化,重新调整mq的订阅");
        List<ExecuteResponse> responses = MQConnectionManager.onSubscriptChange(request.getStrategy());
        return RestResult.buildSuccess(responses);
    }
}
