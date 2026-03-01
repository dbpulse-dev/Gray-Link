package com.dbcat.gray.agent.dubbo;


import com.dbcat.gray.agent.core.context.Context;
import com.dbcat.gray.agent.core.context.DefaultContext;
import com.dbcat.gray.agent.core.context.OutContextInterceptor;
import com.dbcat.gray.agent.core.dto.ComponentType;
import com.dbcat.gray.agent.core.meter.CounterManager;
import com.dbcat.gray.agent.core.plugin.interceptor.enhance.EnhancedInstance;
import com.dbcat.gray.agent.core.plugin.interceptor.enhance.InstanceMethodsAroundInterceptor;
import com.dbcat.gray.agent.core.plugin.interceptor.enhance.MethodInterceptResult;
import com.dbcat.gray.agent.core.util.ServerContextHolder;
import org.apache.dubbo.rpc.RpcContext;

import java.lang.reflect.Method;

import static com.dbcat.gray.agent.core.conf.Config.Agent.ROUTE_LABEL;

/**
 * @author Blackfost
 */
public class ConsumerContextInterceptor implements OutContextInterceptor, InstanceMethodsAroundInterceptor {

    @Override
    public void beforeMethod(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes, MethodInterceptResult result) {
        RpcContext ctx = RpcContext.getContext();
        Context context = new DefaultContext(ctx.getObjectAttachments());
        this.setOutContext(context);
        String routingEnv = (String) ServerContextHolder.get(ROUTE_LABEL);
        CounterManager.increasePublish(ComponentType.API, routingEnv);
    }
}
