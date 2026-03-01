package com.dbcat.gray.agent.threading;


import com.dbcat.gray.agent.core.context.DefaultContext;
import com.dbcat.gray.agent.core.context.InContextInterceptor;
import com.dbcat.gray.agent.core.plugin.interceptor.enhance.EnhancedInstance;
import com.dbcat.gray.agent.core.plugin.interceptor.enhance.InstanceMethodsAroundInterceptor;
import com.dbcat.gray.agent.core.plugin.interceptor.enhance.MethodInterceptResult;

import java.lang.reflect.Method;

/**
 * @author Blackfost
 */
public class ThreadingMethodInterceptor implements InContextInterceptor, InstanceMethodsAroundInterceptor {

    @Override
    public void beforeMethod(final EnhancedInstance objInst, final Method method, final Object[] allArguments,
                             final Class<?>[] argumentsTypes, final MethodInterceptResult result) {
        DefaultContext context = (DefaultContext) objInst.getGrayDynamicField();
        this.setInContext(context);
    }

    @Override
    public Object afterMethod(final EnhancedInstance objInst, final Method method, final Object[] allArguments,
                              final Class<?>[] argumentsTypes, final Object ret) {
        this.clear();
        return ret;
    }
}
