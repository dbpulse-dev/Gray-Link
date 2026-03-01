package com.dbcat.gray.agent.threading.define;

import com.dbcat.gray.agent.core.conf.Config;
import com.dbcat.gray.agent.core.plugin.interceptor.ConstructorInterceptPoint;
import com.dbcat.gray.agent.core.plugin.interceptor.InstanceMethodsInterceptPoint;
import com.dbcat.gray.agent.core.plugin.interceptor.enhance.ClassInstanceMethodsEnhancePluginDefine;
import com.dbcat.gray.agent.core.plugin.match.ClassMatch;
import com.dbcat.gray.agent.core.plugin.match.HierarchyMatch;
import com.dbcat.gray.agent.core.plugin.match.MultiClassNameMatch;
import com.dbcat.gray.agent.core.plugin.match.PrefixMatch;
import com.dbcat.gray.agent.core.plugin.match.logical.LogicalMatchOperation;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

import java.util.List;

/**
 * @author Blackfost
 */
public class ThreadPoolExecutorDefine extends ClassInstanceMethodsEnhancePluginDefine {

    private static final String ENHANCE_CLASS = "java.util.concurrent.ThreadPoolExecutor";

    private static final String INTERCEPT_EXECUTE_METHOD_HANDLE = "com.dbcat.gray.agent.threading.ThreadPoolExecuteMethodInterceptor";

    private static final String INTERCEPT_SUBMIT_METHOD_HANDLE = "com.dbcat.gray.agent.threading.ThreadPoolSubmitMethodInterceptor";

    @Override
    public boolean isBootstrapInstrumentation() {
        return true;
    }

    @Override
    protected ClassMatch enhanceClass() {
        List<String> pks = Config.Agent.ENHANCE_THREAD_POOL_PACKAGES;
        if(pks == null || pks.isEmpty()){
            return LogicalMatchOperation.or(HierarchyMatch.byHierarchyMatch(ENHANCE_CLASS)
                    , MultiClassNameMatch.byMultiClassMatch(ENHANCE_CLASS)
                    , MultiClassNameMatch.byMultiClassMatch("java.util.concurrent.ForkJoinPool"));
        }
        String[] prefixes = new String[pks.size()];
        pks.toArray(prefixes);

        return LogicalMatchOperation.and(HierarchyMatch.byHierarchyMatch(ENHANCE_CLASS),PrefixMatch.nameStartsWith(prefixes));

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
                        return ElementMatchers.named("execute");
                    }

                    @Override
                    public String getMethodsInterceptor() {
                        return INTERCEPT_EXECUTE_METHOD_HANDLE;
                    }

                    @Override
                    public boolean isOverrideArgs() {
                        return true;
                    }
                },
                new InstanceMethodsInterceptPoint() {
                    @Override
                    public ElementMatcher<MethodDescription> getMethodsMatcher() {
                        return ElementMatchers.named("submit");
                    }

                    @Override
                    public String getMethodsInterceptor() {
                        return INTERCEPT_SUBMIT_METHOD_HANDLE;
                    }

                    @Override
                    public boolean isOverrideArgs() {
                        return true;
                    }
                }
        };
    }
}
