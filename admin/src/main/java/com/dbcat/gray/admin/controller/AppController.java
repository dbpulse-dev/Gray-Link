package com.dbcat.gray.admin.controller;


import com.dbcat.gray.admin.dto.*;
import com.dbcat.gray.admin.service.AppInstanceService;
import com.dbcat.gray.admin.service.InstanceTrafficService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping(value = "/app")
public class AppController {

    @Autowired
    private AppInstanceService appInstanceService;

    @Autowired
    private InstanceTrafficService trafficService;

    @PostMapping("/instance/report")
    public RestResult receiveReport(@RequestBody AppInstanceReport report) {
        appInstanceService.onInstanceReport(report);
        return RestResult.buildSuccess();
    }

    @PostMapping("/instance/traffic/report")
    public RestResult trafficReport(@RequestBody InstanceTraffic report) {
        trafficService.save(report);
        return RestResult.buildSuccess();
    }


    @PostMapping("/instance/pull")
    public RestResult<List<InstanceStatus>> listAll(@RequestBody InstancesRequest request) {
        final List<InstanceStatus> instances = appInstanceService.getInstances(request);
        return RestResult.buildSuccess(instances);
    }
}
