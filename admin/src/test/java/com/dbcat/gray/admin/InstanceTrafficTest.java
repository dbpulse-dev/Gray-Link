package com.dbcat.gray.admin;

import com.dbcat.gray.admin.entity.InstanceTrafficEntity;
import com.dbcat.gray.admin.mapper.InstanceTrafficMapper;
import com.dbcat.gray.admin.service.InstanceTrafficService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RunWith(SpringRunner.class)
@Slf4j
@SpringBootTest(classes = {TestApplication.class})
public class InstanceTrafficTest {

    @Autowired
    private InstanceTrafficMapper trafficMapper;


    @Autowired
    private InstanceTrafficService trafficService;

    @Autowired
    private DataSource dataSource;

    String batch_insert_sql = "INSERT INTO instance_traffic (app_name, uuid,type,name,value) VALUES (?,?,?,?,?)";

    @Test
    public void batchInsert() {
        List<InstanceTrafficEntity> entities = mockEntities(100);
        trafficService.batchInsert(entities);
    }


    @Test
    public void batchUpdate() {
        List<InstanceTrafficEntity> entities = new ArrayList<>();
        InstanceTrafficEntity entity1 = new InstanceTrafficEntity();
        entity1.setId(1);
        entity1.setValue(12);
        InstanceTrafficEntity entity2 = new InstanceTrafficEntity();
        entity2.setId(2);
        entity2.setValue(444);
        entities.add(entity1);
        entities.add(entity2);
        trafficMapper.batchUpdate(entities);
    }


    private List<InstanceTrafficEntity> mockEntities(int count) {
        List<InstanceTrafficEntity> entities = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            InstanceTrafficEntity entity = new InstanceTrafficEntity();
            entity.setUuid(UUID.randomUUID().toString().replace("-", ""));
            entity.setValue(123);
            entity.setType(1);
            entity.setAppName("test-app");
            entity.setName("rabbit-count");
            entities.add(entity);
        }
        return entities;

    }

    @Test
    public void update() {

    }
}
