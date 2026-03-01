package com.dbcat.gray.agent.cloud.define;


import com.dbcat.gray.agent.core.plugin.interceptor.ConstructorInterceptPoint;
import com.dbcat.gray.agent.core.plugin.interceptor.InstanceMethodsInterceptPoint;
import com.dbcat.gray.agent.core.plugin.interceptor.enhance.ClassInstanceMethodsEnhancePluginDefine;
import com.dbcat.gray.agent.core.plugin.match.ClassMatch;
import com.dbcat.gray.agent.core.plugin.match.HierarchyMatch;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

/**
 * 路由拦截处理
 *
 * @author Blackfost
 */
public class RulePluginDefine extends ClassInstanceMethodsEnhancePluginDefine {
    private static final String ENHANCE_CLASS = "com.netflix.loadbalancer.AbstractLoadBalancerRule";
    private static final String INTERCEPTOR_CLASS = "com.dbcat.gray.agent.cloud.RuleInterceptor";

    @Override
    protected ClassMatch enhanceClass() {
        return HierarchyMatch.byHierarchyMatch(ENHANCE_CLASS);
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
                        return ElementMatchers.named("choose");
                    }

                    @Override
                    public String getMethodsInterceptor() {
                        return INTERCEPTOR_CLASS;
                    }
                }
        };
    }
}
