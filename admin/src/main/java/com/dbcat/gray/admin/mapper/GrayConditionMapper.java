package com.dbcat.gray.admin.mapper;


import com.dbcat.gray.admin.dto.GrayConditionRequest;
import com.dbcat.gray.admin.entity.GrayConditionEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GrayConditionMapper {

    GrayConditionEntity getExists(GrayConditionEntity entity);

    void add(GrayConditionEntity entity);

    void deleteById(@Param("id") Integer id);


    GrayConditionEntity getById(@Param("id") Integer id);

    void update(GrayConditionEntity entity);

    void updateAccessCount(GrayConditionEntity updateCount);

    List<GrayConditionEntity> list(GrayConditionRequest request);

}
