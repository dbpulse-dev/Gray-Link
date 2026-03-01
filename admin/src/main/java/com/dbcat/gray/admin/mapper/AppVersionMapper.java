package com.dbcat.gray.admin.mapper;


import com.dbcat.gray.admin.dto.AppVersionRequest;
import com.dbcat.gray.admin.dto.AppVersionStatusUpdate;
import com.dbcat.gray.admin.entity.AppInstanceEntity;
import com.dbcat.gray.admin.entity.AppVersionEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AppVersionMapper {

    Integer add(AppVersionEntity entity);

    List<AppVersionEntity> list(AppVersionRequest request);

    void updateStatus(AppVersionStatusUpdate entity);

    void updateByReport(AppInstanceEntity entity);

    AppVersionEntity getByNameAndVersion(@Param("uuid") String uuid);


    List<AppVersionEntity> listByNames(@Param("appNames") List<String> appNames);
}
