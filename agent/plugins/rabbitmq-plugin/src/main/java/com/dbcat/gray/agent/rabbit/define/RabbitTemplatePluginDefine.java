package com.dbcat.gray.agent.rabbit.define;


import com.dbcat.gray.agent.core.plugin.interceptor.ConstructorInterceptPoint;
import com.dbcat.gray.agent.core.plugin.interceptor.InstanceMethodsInterceptPoint;
import com.dbcat.gray.agent.core.plugin.interceptor.enhance.ClassInstanceMethodsEnhancePluginDefine;
import com.dbcat.gray.agent.core.plugin.match.ClassMatch;
import com.dbcat.gray.agent.core.plugin.match.NameMatch;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

import static net.bytebuddy.matcher.ElementMatchers.takesArguments;

/**
 * @author Blackfost
 */
public class RabbitTemplatePluginDefine extends ClassInstanceMethodsEnhancePluginDefine {

    public static final String ENHANCE_CLASS = "org.springframework.amqp.rabbit.core.RabbitTemplate";

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
                        //续传生产端路由变量
                        return ElementMatchers.named("send").and(ElementMatchers.takesArguments(4));
                    }

                    @Override
                    public String getMethodsInterceptor() {
                        return "com.dbcat.gray.agent.rabbit.MessagePublishInterceptor";
                    }

                    @Override
                    public boolean isOverrideArgs() {
                        return false;
                    }
                },
                new InstanceMethodsInterceptPoint() {
                    @Override
                    public ElementMatcher<MethodDescription> getMethodsMatcher() {
                        //拦截据环境选择connection factory
                        return ElementMatchers.named("doExecute").and(takesArguments(2));
                    }

                    @Override
                    public String getMethodsInterceptor() {
                        return "com.dbcat.gray.agent.rabbit.RabbitTemplateDoExecuteInterceptor";
                    }

                    @Override
                    public boolean isOverrideArgs() {
                        return true;
                    }
                }
        };
    }

    @Override
    protected ClassMatch enhanceClass() {
        return NameMatch.byName(ENHANCE_CLASS);
    }
}
