package com.dbcat.gray.agent.threading;


import com.dbcat.gray.agent.core.plugin.interceptor.enhance.EnhancedInstance;
import com.dbcat.gray.agent.core.plugin.interceptor.enhance.InstanceMethodsAroundInterceptor;
import com.dbcat.gray.agent.core.plugin.interceptor.enhance.MethodInterceptResult;

import java.lang.reflect.Method;

/**
 * @author Blackfost
 */
public abstract class AbstractThreadPoolInterceptor implements InstanceMethodsAroundInterceptor {

    @Override
    public void beforeMethod(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes, MethodInterceptResult result) throws Throwable {
        if (notToEnhance(allArguments)) {
            return;
        }
        Object wrappedObject = wrap(allArguments[0]);
        if (wrappedObject != null) {
            allArguments[0] = wrappedObject;
        }
    }


    public abstract Object wrap(Object param);

    private boolean notToEnhance(Object[] allArguments) {
        if (allArguments == null || allArguments.length < 1) {
            return true;
        }
        Object argument = allArguments[0];
        //如果已经被增强过，不必在增强了
        return argument instanceof EnhancedInstance;
    }
}
