package com.dbcat.gray.admin.service;

import com.dbcat.gray.admin.dto.InstanceTraffic;
import com.dbcat.gray.admin.entity.InstanceTrafficEntity;

import java.util.List;

public interface InstanceTrafficService {

    void save(InstanceTraffic report);

    void batchInsert(List<InstanceTrafficEntity> entities);
}
