package com.dbcat.gray.agent.cloud;

import com.dbcat.gray.agent.core.AppInstance;
import com.dbcat.gray.agent.core.conf.GrayConstant;
import com.dbcat.gray.agent.core.plugin.interceptor.enhance.EnhancedInstance;
import com.dbcat.gray.agent.core.plugin.interceptor.enhance.InstanceConstructorInterceptor;
import com.netflix.appinfo.InstanceInfo;

import java.util.Map;

import static com.dbcat.gray.agent.core.conf.GrayConstant.APP_NAME;

/**
 * eureka注册中心，实例id 作为元数据上报
 *
 * @author Blackfost
 */
public class InstanceInfoInterceptor implements InstanceConstructorInterceptor {

    @Override
    public void onConstruct(EnhancedInstance objInst, Object[] allArguments) throws Throwable {
        AppInstance instance = AppInstance.getInstance();
        InstanceInfo instanceInfo = (InstanceInfo) objInst;
        Map<String, String> metadata = instanceInfo.getMetadata();
        metadata.put(GrayConstant.INSTANCE_UUID, instance.getUuid());
        metadata.put(APP_NAME, instance.getAppName());
    }
}
