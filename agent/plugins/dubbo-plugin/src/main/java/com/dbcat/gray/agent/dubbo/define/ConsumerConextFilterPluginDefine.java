package com.dbcat.gray.agent.dubbo.define;


import com.dbcat.gray.agent.core.plugin.interceptor.ConstructorInterceptPoint;
import com.dbcat.gray.agent.core.plugin.interceptor.InstanceMethodsInterceptPoint;
import com.dbcat.gray.agent.core.plugin.interceptor.enhance.ClassInstanceMethodsEnhancePluginDefine;
import com.dbcat.gray.agent.core.plugin.match.ClassMatch;
import com.dbcat.gray.agent.core.plugin.match.NameMatch;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;


/**
 * @author Blackfost
 */
public class ConsumerConextFilterPluginDefine extends ClassInstanceMethodsEnhancePluginDefine {

    private static final String ENHANCE_CLASS = "org.apache.dubbo.rpc.filter.ConsumerContextFilter";

    private static final String INTERCEPTOR_CLASS = "com.dbcat.gray.agent.dubbo.ConsumerContextInterceptor";

    @Override
    protected ClassMatch enhanceClass() {
        return NameMatch.byName(ENHANCE_CLASS);
    }

    @Override
    public ConstructorInterceptPoint[] getConstructorsInterceptPoints() {
        return new ConstructorInterceptPoint[0];
    }

    @Override
    public InstanceMethodsInterceptPoint[] getInstanceMethodsInterceptPoints() {
        return new InstanceMethodsInterceptPoint[]{
                new InstanceMethodsInterceptPoint() {
                    @Override
                    public ElementMatcher<MethodDescription> getMethodsMatcher() {
                        return ElementMatchers.named("invoke");
                    }

                    @Override
                    public String getMethodsInterceptor() {
                        return INTERCEPTOR_CLASS;
                    }
                }
        };
    }
}

