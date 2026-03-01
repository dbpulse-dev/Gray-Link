package com.dbcat.gray.agent.cloud;

import com.dbcat.gray.agent.core.context.Context;
import com.dbcat.gray.agent.core.context.OutContextInterceptor;
import com.dbcat.gray.agent.core.plugin.interceptor.enhance.EnhancedInstance;
import com.dbcat.gray.agent.core.plugin.interceptor.enhance.InstanceMethodsAroundInterceptor;
import com.dbcat.gray.agent.core.plugin.interceptor.enhance.MethodInterceptResult;
import feign.RequestTemplate;

import java.lang.reflect.Method;

/**
 * @author Blackfost
 */
public class FeignContextInterceptor implements OutContextInterceptor, InstanceMethodsAroundInterceptor {

    @Override
    public void beforeMethod(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes, MethodInterceptResult result) throws Throwable {
        RequestTemplate template = (RequestTemplate) allArguments[0];
        Context context = buildContext(template);
        this.setOutContext(context);
    }

    private Context buildContext(RequestTemplate template) {
        return new Context() {
            @Override
            public void put(String key, Object value) {
                template.header(key, (String) value);
            }

            @Override
            public Object get(String key) {
                return template.headers().get(key);
            }
        };
    }

}
