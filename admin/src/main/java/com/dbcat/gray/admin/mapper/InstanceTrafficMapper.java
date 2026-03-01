package com.dbcat.gray.admin.mapper;


import com.dbcat.gray.admin.dto.GrayConditionRequest;
import com.dbcat.gray.admin.entity.InstanceTrafficEntity;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface InstanceTrafficMapper {

    void batchUpdate(List<InstanceTrafficEntity> entities);

    List<InstanceTrafficEntity> list(GrayConditionRequest request);

    List<InstanceTrafficEntity> listByUuid(@Param("uuid") String uuid);

    void setUpdate(@Param("uuid") String uuid);

    Integer deleteByReportAt(@Param("reportAt") Date reportAt);


}
