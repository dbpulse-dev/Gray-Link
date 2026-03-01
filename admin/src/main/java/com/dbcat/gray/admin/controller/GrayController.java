package com.dbcat.gray.admin.controller;


import com.dbcat.gray.admin.dto.*;
import com.dbcat.gray.admin.service.AppInstanceService;
import com.dbcat.gray.admin.service.GrayControlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/gray")
public class GrayController {


    @Autowired
    private GrayControlService controlService;

    @Autowired
    private AppInstanceService appInstanceService;

    /**
     * 更新应用所有实例状态
     * @param request
     * @return
     */
    @PostMapping("/app/status/update")
    public RestResult statusUpdate(@RequestBody @Validated AppStatusUpdate request) {
        controlService.updateAppStatus(request);
        return RestResult.buildSuccess();
    }

    /**
     * 更新实例状态
     * @param request
     * @return
     */
    @PostMapping("/instance/status/update")
    public RestResult statusUpdate(@RequestBody @Validated InstanceStatusUpdate request) {
        controlService.updateInstanceStatus(request);
        return RestResult.buildSuccess();
    }

    @PostMapping("/app/status/batch-update")
    public RestResult batchUpdateStatus(@RequestBody @Validated AppStatusBatchUpdate request) {
        controlService.batchUpdateAppStatus(request);
        return RestResult.buildSuccess();
    }

    @PostMapping("/instance/subscript/status")
    public RestResult<InstanceSubscriptStatus> getSubscriptStatus(@RequestBody SubscriptStatusRequest request) {
        InstanceSubscriptStatus subscriptStatus = appInstanceService.getSubscriptStatus(request);
        return RestResult.buildSuccess(subscriptStatus);
    }

    @PostMapping("/instance/subscript-status/update")
    public RestResult<List<SubscriptResponse>> updateSubscriptStatus(@RequestBody @Validated InstanceSubscriptStatusUpdate request) {
        List<SubscriptResponse> subscriptResponses = controlService.updateSubscriptStatus(request);
        return RestResult.buildSuccess(subscriptResponses);
    }


    @PostMapping("/app/deploy/env-status")
    public RestResult<ServerDeployEnv> deployEnvStatus(@RequestBody @Validated AppDeployRequest request) {
        ServerDeployEnv env = new ServerDeployEnv();
        return RestResult.buildSuccess(env);
    }

}
