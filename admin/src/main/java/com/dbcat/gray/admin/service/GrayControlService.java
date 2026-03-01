package com.dbcat.gray.admin.service;

import com.dbcat.gray.admin.dto.*;

import java.util.List;

public interface GrayControlService {

    void updateInstanceStatus(InstanceStatusUpdate update);

    List<SubscriptResponse> updateSubscriptStatus(InstanceSubscriptStatusUpdate update);

    void updateAppStatus(AppStatusUpdate update);

    void batchUpdateAppStatus(AppStatusBatchUpdate batchUpdate);
}
