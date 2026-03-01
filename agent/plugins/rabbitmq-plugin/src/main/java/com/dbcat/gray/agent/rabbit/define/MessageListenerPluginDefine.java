package com.dbcat.gray.agent.rabbit.define;


import com.dbcat.gray.agent.core.plugin.interceptor.ConstructorInterceptPoint;
import com.dbcat.gray.agent.core.plugin.interceptor.InstanceMethodsInterceptPoint;
import com.dbcat.gray.agent.core.plugin.interceptor.enhance.ClassInstanceMethodsEnhancePluginDefine;
import com.dbcat.gray.agent.core.plugin.match.ClassMatch;
import com.dbcat.gray.agent.core.plugin.match.HierarchyMatch;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

/**
 * @author Blackfost
 */
public class MessageListenerPluginDefine extends ClassInstanceMethodsEnhancePluginDefine {

    public static final String ENHANCE_CLASS = "org.springframework.amqp.core.MessageListener";

    @Override
    public ConstructorInterceptPoint[] getConstructorsInterceptPoints() {
        return new ConstructorInterceptPoint[0];
    }

    @Override
    protected ClassMatch enhanceClass() {
        return HierarchyMatch.byHierarchyMatch(ENHANCE_CLASS);
    }

    @Override
    public InstanceMethodsInterceptPoint[] getInstanceMethodsInterceptPoints() {
        return new InstanceMethodsInterceptPoint[]{
                new InstanceMethodsInterceptPoint() {
                    @Override
                    public ElementMatcher<MethodDescription> getMethodsMatcher() {
                        return ElementMatchers.named("onMessage").or(ElementMatchers.named("onMessageBatch"));
                    }

                    @Override
                    public String getMethodsInterceptor() {
                        return "com.dbcat.gray.agent.rabbit.MessageListenerInterceptor";
                    }

                    @Override
                    public boolean isOverrideArgs() {
                        return false;
                    }
                }
        };
    }


}
