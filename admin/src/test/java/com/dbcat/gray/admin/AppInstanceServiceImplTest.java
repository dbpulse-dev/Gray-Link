package com.dbcat.gray.admin;

import com.dbcat.gray.admin.dto.AppInstancePageRequest;
import com.dbcat.gray.admin.dto.AppInstanceResponse;
import com.dbcat.gray.admin.entity.AppInstanceEntity;
import com.dbcat.gray.admin.mapper.AppInstanceMapper;
import com.dbcat.gray.admin.service.AppInstanceService;
import com.github.pagehelper.Page;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

@RunWith(SpringRunner.class)
@Slf4j
@SpringBootTest(classes = {TestApplication.class})
public class AppInstanceServiceImplTest {

    @Autowired
    private AppInstanceMapper appInstanceMapper;

    @Autowired
    private AppInstanceService appInstanceService;

    @Test
    public void add() {
        AppInstanceEntity entity = new AppInstanceEntity();
        entity.setAppName("test-app");
        entity.setStatus(1);
        entity.setEnvStatus(1);
        entity.setAgentPort(1688);
        entity.setUuid(UUID.randomUUID().toString().replace("-", ""));
        entity.setIp("192.168.3.1");
        appInstanceMapper.add(entity);
    }

    @Test
    public void getByUuid() {
        AppInstanceResponse instance = appInstanceMapper.getByUuid("26071f49c4534005a92c190332d9ec17");
        assert instance != null;
    }

    @Test
    public void listByPage() {
        AppInstancePageRequest request = new AppInstancePageRequest();
        Page<AppInstanceResponse> page = appInstanceService.listByPage(request);
        assert page.getResult().size() > 0;
    }


}
