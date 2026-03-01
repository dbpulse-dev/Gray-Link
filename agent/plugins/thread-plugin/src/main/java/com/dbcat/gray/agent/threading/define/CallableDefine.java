package com.dbcat.gray.agent.threading.define;

import com.dbcat.gray.agent.core.plugin.interceptor.ConstructorInterceptPoint;
import com.dbcat.gray.agent.core.plugin.interceptor.InstanceMethodsInterceptPoint;
import com.dbcat.gray.agent.core.plugin.interceptor.StaticMethodsInterceptPoint;
import com.dbcat.gray.agent.core.plugin.interceptor.enhance.ClassEnhancePluginDefine;
import com.dbcat.gray.agent.core.plugin.match.ClassMatch;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;

import static net.bytebuddy.matcher.ElementMatchers.*;

/**
 * @author Blackfost
 */
public class CallableDefine extends ClassEnhancePluginDefine {
    private static final String CALLABLE_CLASS = "java.util.concurrent.Callable";
    private static final String CALLABLE_CLASS_INTERCEPTOR = "com.dbcat.gray.agent.threading.ThreadingConstructorInterceptor";
    private static final String CALLABLE_CALL_METHOD_INTERCEPTOR = "com.dbcat.gray.agent.threading.ThreadingMethodInterceptor";

    @Override
    protected ClassMatch enhanceClass() {
        return new ThreadingMatch(CALLABLE_CLASS);
    }

    @Override
    public ConstructorInterceptPoint[] getConstructorsInterceptPoints() {
        return new ConstructorInterceptPoint[]{
                new ConstructorInterceptPoint() {
                    @Override
                    public ElementMatcher<MethodDescription> getConstructorMatcher() {
                        return any();
                    }

                    @Override
                    public String getConstructorInterceptor() {
                        return CALLABLE_CLASS_INTERCEPTOR;
                    }
                }
        };
    }

    @Override
    public InstanceMethodsInterceptPoint[] getInstanceMethodsInterceptPoints() {
        return new InstanceMethodsInterceptPoint[]{
                new InstanceMethodsInterceptPoint() {
                    @Override
                    public ElementMatcher<MethodDescription> getMethodsMatcher() {
                        //拦截call方法
                        return named("call").and(takesArguments(0));
                    }

                    @Override
                    public String getMethodsInterceptor() {
                        return CALLABLE_CALL_METHOD_INTERCEPTOR;
                    }

                    @Override
                    public boolean isOverrideArgs() {
                        return false;
                    }
                }
        };
    }

    @Override
    public StaticMethodsInterceptPoint[] getStaticMethodsInterceptPoints() {
        return new StaticMethodsInterceptPoint[0];
    }

    @Override
    public boolean isBootstrapInstrumentation() {
        return true;
    }
}