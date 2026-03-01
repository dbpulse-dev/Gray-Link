package com.dbcat.gray.admin.mapper;


import com.dbcat.gray.admin.dto.AppInstanceRequest;
import com.dbcat.gray.admin.dto.AppInstanceResponse;
import com.dbcat.gray.admin.entity.AppInstanceEntity;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface AppInstanceMapper {

    Integer add(AppInstanceEntity entity);


    List<AppInstanceResponse> list(AppInstanceRequest request);


    AppInstanceResponse getById(@Param("id") Integer id);

    Integer deleteById(@Param("id") Integer id);

    Integer deleteByReportAt(@Param("reportAt") Date reportAt);

    void updateByCheck(AppInstanceEntity entity);

    void updateByReport(AppInstanceEntity entity);

    AppInstanceResponse getByUuid(@Param("uuid") String uuid);

    List<AppInstanceResponse> listStatusByName(@Param("appName") String appName);

    List<AppInstanceResponse> listByNames(@Param("appNames") List<String> appNames);
}
