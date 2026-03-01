package com.dbcat.gray.admin.service;

import com.dbcat.gray.admin.dto.GrayConditionAdd;
import com.dbcat.gray.admin.dto.GrayConditionRequest;
import com.dbcat.gray.admin.dto.GrayConditionUpdate;
import com.dbcat.gray.admin.entity.GrayConditionEntity;

import java.util.List;

public interface GrayConditionService {

    List<GrayConditionEntity> list(GrayConditionRequest request);

    void add(GrayConditionAdd conditionAdd);

    void update(GrayConditionUpdate update);
}
