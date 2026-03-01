package com.dbcat.gray.agent.dubbo;

import com.dbcat.gray.agent.core.AppInstance;
import com.dbcat.gray.agent.core.conf.GrayConstant;
import com.dbcat.gray.agent.core.plugin.interceptor.enhance.StaticMethodsAroundInterceptor;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * 打标记元数
 *
 * @author Blackfost
 */
public class AbstractInterfaceConfigInterceptor implements StaticMethodsAroundInterceptor {

    @Override
    public Object afterMethod(Class clazz, Method method, Object[] allArguments, Class<?>[] parameterTypes, Object ret) {
        Map<String, String> params = (Map) allArguments[0];
        String side = params.get("side");
        if ("provider".equals(side)) {
            AppInstance instance = AppInstance.getInstance();
            params.put(GrayConstant.INSTANCE_UUID, instance.getUuid());
        }
        return ret;
    }

}
