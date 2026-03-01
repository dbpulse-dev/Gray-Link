package com.dbcat.gray.agent.threading;


import com.dbcat.gray.agent.core.context.DefaultContext;
import com.dbcat.gray.agent.core.plugin.interceptor.enhance.EnhancedInstance;
import com.dbcat.gray.agent.core.plugin.interceptor.enhance.InstanceConstructorInterceptor;

/**
 * @author Blackfost
 */
public class ThreadingConstructorInterceptor implements InstanceConstructorInterceptor {

    @Override
    public void onConstruct(final EnhancedInstance objInst, final Object[] allArguments) {
        DefaultContext defaultContext = DefaultContext.getDefaultContext();
        objInst.setGrayDynamicField(defaultContext);
    }
}
