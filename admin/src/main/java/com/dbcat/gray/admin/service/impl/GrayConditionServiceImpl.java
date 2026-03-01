package com.dbcat.gray.admin.service.impl;

import com.dbcat.gray.admin.dto.GrayConditionAdd;
import com.dbcat.gray.admin.dto.GrayConditionRequest;
import com.dbcat.gray.admin.dto.GrayConditionUpdate;
import com.dbcat.gray.admin.entity.GrayConditionEntity;
import com.dbcat.gray.admin.mapper.GrayConditionMapper;
import com.dbcat.gray.admin.service.GrayConditionService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GrayConditionServiceImpl implements GrayConditionService {

    @Autowired
    private GrayConditionMapper grayConditionMapper;

    @Override
    public List<GrayConditionEntity> list(GrayConditionRequest request) {
        return grayConditionMapper.list(request);
    }

    @Override
    public void add(GrayConditionAdd conditionAdd) {
        GrayConditionEntity entity = new GrayConditionEntity();
        BeanUtils.copyProperties(conditionAdd, entity);
        GrayConditionEntity exists = grayConditionMapper.getExists(entity);
        if (exists != null) {
            throw new RuntimeException("条件已存在");
        }
        grayConditionMapper.add(entity);
    }

    @Override
    public void update(GrayConditionUpdate update) {
        GrayConditionEntity entity = grayConditionMapper.getById(update.getId());
        entity.setType(update.getType());
        entity.setEnable(update.getEnable());
        entity.setValue(update.getValue());
        grayConditionMapper.update(entity);
    }
}
