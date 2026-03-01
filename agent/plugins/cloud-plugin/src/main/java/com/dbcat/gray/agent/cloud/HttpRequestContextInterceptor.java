package com.dbcat.gray.agent.cloud;

import com.dbcat.gray.agent.core.context.Context;
import com.dbcat.gray.agent.core.context.OutContextInterceptor;
import com.dbcat.gray.agent.core.plugin.interceptor.enhance.EnhancedInstance;
import com.dbcat.gray.agent.core.plugin.interceptor.enhance.InstanceMethodsAroundInterceptor;
import com.dbcat.gray.agent.core.plugin.interceptor.enhance.MethodInterceptResult;
import org.springframework.http.HttpRequest;

import java.lang.reflect.Method;


/**
 * 使用restTemplate 走ribbon负载
 *
 * @author Blackfost
 */
public class HttpRequestContextInterceptor implements OutContextInterceptor, InstanceMethodsAroundInterceptor {

    @Override
    public void beforeMethod(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes, MethodInterceptResult result) throws Throwable {
        HttpRequest request = (HttpRequest) allArguments[0];
        Context context = buildContext(request);
        this.setOutContext(context);
    }

    private Context buildContext(HttpRequest request) {
        return new Context() {
            @Override
            public void put(String key, Object value) {
                request.getHeaders().set(key, (String) value);
            }

            @Override
            public Object get(String key) {
                return request.getHeaders().get(key);
            }
        };
    }

}
