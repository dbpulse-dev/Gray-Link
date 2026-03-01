package com.dbcat.gray.agent.dubbo;


import com.dbcat.gray.agent.core.plugin.interceptor.enhance.EnhancedInstance;
import com.dbcat.gray.agent.core.plugin.interceptor.enhance.InstanceMethodsAroundInterceptor;
import com.dbcat.gray.agent.core.plugin.interceptor.enhance.MethodInterceptResult;
import com.dbcat.gray.agent.core.util.ServerContextHolder;
import org.apache.dubbo.rpc.Invoker;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import static com.dbcat.gray.agent.core.conf.Config.Agent.ROUTE_LABEL;

/**
 * @author Blackfost
 */
public class AppGrayRouterInterceptor implements InstanceMethodsAroundInterceptor {

    @Override
    public void beforeMethod(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes, MethodInterceptResult result) {
        List<Invoker> invokers = (List<Invoker>) allArguments[0];
        if (invokers.isEmpty()) {
            result.defineReturnValue(Collections.emptyList());
            return;
        }
        String env = (String) ServerContextHolder.get(ROUTE_LABEL);
        DubboServerSelector selector = DubboServerSelector.build(env, invokers);
        List<Invoker> targetInvokers = selector.selectServers();
        result.defineReturnValue(targetInvokers);
    }
}
