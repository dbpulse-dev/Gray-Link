package com.dbcat.gray.admin;

import com.dbcat.gray.admin.dto.GrayConditionAdd;
import com.dbcat.gray.admin.dto.GrayConditionUpdate;
import com.dbcat.gray.admin.service.GrayConditionService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@Slf4j
@SpringBootTest(classes = {TestApplication.class})
public class GrayConditionServiceTest {

    @Autowired
    private GrayConditionService conditionService;


    @Test
    public void add() {
        GrayConditionAdd conditionAdd = new GrayConditionAdd();
        conditionAdd.setType("user");
        conditionAdd.setValue("abc123");
        conditionAdd.setEnable(1);
        conditionService.add(conditionAdd);
    }

    @Test
    public void update() {
        GrayConditionUpdate conditionAdd = new GrayConditionUpdate();
        conditionAdd.setId(1);
        conditionAdd.setValue("testabc123");
        conditionAdd.setEnable(2);
        conditionService.update(conditionAdd);
    }
}
