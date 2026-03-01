package com.dbcat.gray.agent.cloud;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.dbcat.gray.agent.core.AppInstance;
import com.dbcat.gray.agent.core.conf.GrayConstant;
import com.dbcat.gray.agent.core.plugin.interceptor.enhance.EnhancedInstance;
import com.dbcat.gray.agent.core.plugin.interceptor.enhance.InstanceConstructorInterceptor;

import java.util.Map;


/**
 * nacos 注册中心，实例id 作为元数据上报
 *
 * @author Blackfost
 */
public class NacosDiscoveryPropertiesInterceptor implements InstanceConstructorInterceptor {

    @Override
    public void onConstruct(EnhancedInstance objInst, Object[] allArguments) throws Throwable {
        AppInstance instance = AppInstance.getInstance();
        NacosDiscoveryProperties properties = (NacosDiscoveryProperties) objInst;
        Map<String, String> metadata = properties.getMetadata();
        metadata.put(GrayConstant.INSTANCE_UUID, instance.getUuid());
        metadata.put(GrayConstant.APP_NAME, instance.getAppName());
    }
}
