package com.dbcat.gray.admin.service;

import com.dbcat.gray.admin.dto.*;
import com.github.pagehelper.Page;

import java.util.List;

public interface AppInstanceService {

    void onInstanceReport(AppInstanceReport report);

    Page<AppInstanceResponse> listByPage(AppInstancePageRequest request);


    List<InstanceStatus> getInstances(InstancesRequest request);


    InstanceSubscriptStatus getSubscriptStatus(SubscriptStatusRequest request);
}
