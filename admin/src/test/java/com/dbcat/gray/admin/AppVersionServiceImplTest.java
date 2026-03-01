package com.dbcat.gray.admin;

import com.dbcat.gray.admin.dto.AppInstancePageRequest;
import com.dbcat.gray.admin.dto.AppInstanceResponse;
import com.dbcat.gray.admin.dto.AppVersionRequest;
import com.dbcat.gray.admin.entity.AppVersionEntity;
import com.dbcat.gray.admin.mapper.AppVersionMapper;
import com.dbcat.gray.admin.service.AppInstanceService;
import com.github.pagehelper.Page;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@Slf4j
@SpringBootTest(classes = {TestApplication.class})
public class AppVersionServiceImplTest {

    @Autowired
    private AppVersionMapper appInstanceMapper;

    @Autowired
    private AppInstanceService appInstanceService;

    @Test
    public void add() {
        AppVersionEntity entity = new AppVersionEntity();
        entity.setEnv("dev");
        entity.setAppName("test-app");
        entity.setEnvStatus(1);
        entity.setVersion("test-20250807");
        appInstanceMapper.add(entity);
    }


    @Test
    public void list() {
        AppVersionRequest request = new AppVersionRequest();
        List<AppVersionEntity> list = appInstanceMapper.list(request);
        assert list.size() > 0;
    }

    @Test
    public void listByPage() {
        AppInstancePageRequest request = new AppInstancePageRequest();
        Page<AppInstanceResponse> page = appInstanceService.listByPage(request);
        assert page.getResult().size() > 0;
    }


}
