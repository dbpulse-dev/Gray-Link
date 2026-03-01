package com.dbcat.gray.agent.core.server.endpoint;


import com.dbcat.gray.agent.core.AppInstance;
import com.dbcat.gray.agent.core.InstanceReportService;
import com.dbcat.gray.agent.core.dto.ExecuteResponse;
import com.dbcat.gray.agent.core.dto.InstanceEnvUpdateRequest;
import com.dbcat.gray.agent.core.logging.api.ILog;
import com.dbcat.gray.agent.core.logging.api.LogManager;
import com.dbcat.gray.agent.core.server.RestResult;

import java.util.List;


/**
 * 更新实例状态
 *
 * @author Blackfost
 */
public class EnvStatusUpdateEndpoint implements Endpoint<InstanceEnvUpdateRequest, List<ExecuteResponse>> {

    private static final ILog log = LogManager.getLogger(EnvStatusUpdateEndpoint.class);

    @Override
    public String path() {
        return "/env-status/update";
    }

    @Override
    public RestResult invoke(InstanceEnvUpdateRequest request) throws Exception {
        log.info("更新实例信息");
        AppInstance.getInstance().setEnvStatus(request.getEnvStatus());
        InstanceReportService.reportInstance();
        return RestResult.buildSuccess();
    }
}
