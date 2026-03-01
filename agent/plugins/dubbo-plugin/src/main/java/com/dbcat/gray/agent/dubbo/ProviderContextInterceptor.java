package com.dbcat.gray.agent.dubbo;


import com.dbcat.gray.agent.core.context.Context;
import com.dbcat.gray.agent.core.context.DefaultContext;
import com.dbcat.gray.agent.core.context.InContextInterceptor;
import com.dbcat.gray.agent.core.meter.CounterManager;
import com.dbcat.gray.agent.core.plugin.interceptor.enhance.EnhancedInstance;
import com.dbcat.gray.agent.core.plugin.interceptor.enhance.InstanceMethodsAroundInterceptor;
import com.dbcat.gray.agent.core.plugin.interceptor.enhance.MethodInterceptResult;
import com.dbcat.gray.agent.core.util.ServerContextHolder;
import org.apache.dubbo.rpc.Invocation;

import java.lang.reflect.Method;

import static com.dbcat.gray.agent.core.conf.Config.Agent.ROUTE_LABEL;
import static com.dbcat.gray.agent.core.dto.ComponentType.API;


/**
 * @author Blackfost
 */
public class ProviderContextInterceptor implements InContextInterceptor, InstanceMethodsAroundInterceptor {

    @Override
    public void beforeMethod(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes, MethodInterceptResult result) throws Throwable {
        Invocation invocation = (Invocation) allArguments[1];

        Context context = new DefaultContext(invocation.getObjectAttachments());
        setInContext(context);
        String routingEnv = (String) ServerContextHolder.get(ROUTE_LABEL);
        CounterManager.increaseConsume(API, routingEnv);
    }

    @Override
    public Object afterMethod(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes, Object ret) throws Throwable {
        this.clear();
        return ret;
    }
}
