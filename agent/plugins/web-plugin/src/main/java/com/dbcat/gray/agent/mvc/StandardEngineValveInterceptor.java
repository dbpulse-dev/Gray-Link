package com.dbcat.gray.agent.mvc;

import com.dbcat.gray.agent.core.context.Context;
import com.dbcat.gray.agent.core.context.InContextInterceptor;
import com.dbcat.gray.agent.core.dto.ComponentType;
import com.dbcat.gray.agent.core.meter.CounterManager;
import com.dbcat.gray.agent.core.plugin.interceptor.enhance.EnhancedInstance;
import com.dbcat.gray.agent.core.plugin.interceptor.enhance.InstanceMethodsAroundInterceptor;
import com.dbcat.gray.agent.core.plugin.interceptor.enhance.MethodInterceptResult;
import com.dbcat.gray.agent.core.util.ReflectUtils;

import java.lang.reflect.Method;

import static com.dbcat.gray.agent.core.conf.Config.Agent.ROUTE_LABEL;

/**
 * @author Blackfost
 */
public class StandardEngineValveInterceptor implements InContextInterceptor, InstanceMethodsAroundInterceptor {

    @Override
    public void beforeMethod(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes, MethodInterceptResult result) {
        Context context = buildContext(allArguments);
        setInContext(context);
        Object routingEnv = context.get(ROUTE_LABEL);
        CounterManager.increaseConsume(ComponentType.API, routingEnv);
    }

    @Override
    public Object afterMethod(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes, Object ret) {
        this.clear();
        return ret;
    }


    private Context buildContext(Object[] allArguments) {
        Object request = allArguments[0];
        Method getHeader = ReflectUtils.getMethod(request, "getHeader", String.class);
        return new Context() {
            @Override
            public void put(String key, Object value) {

            }

            @Override
            public Object get(String key) {
                return ReflectUtils.invokeMethod(getHeader, request, key);
            }
        };
    }
}
