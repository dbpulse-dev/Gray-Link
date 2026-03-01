package com.dbcat.gray.agent.cloud;

import com.dbcat.gray.agent.core.plugin.interceptor.enhance.EnhancedInstance;
import com.dbcat.gray.agent.core.plugin.interceptor.enhance.InstanceMethodsAroundInterceptor;
import com.dbcat.gray.agent.core.plugin.interceptor.enhance.MethodInterceptResult;
import com.dbcat.gray.agent.core.server.ServerSelector;
import com.dbcat.gray.agent.core.util.ServerContextHolder;
import com.google.common.base.Optional;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.ZoneAvoidanceRule;

import java.lang.reflect.Method;
import java.util.List;

import static com.dbcat.gray.agent.core.conf.Config.Agent.ROUTE_LABEL;

/**
 * @author Blackfost
 */
public class RuleInterceptor implements InstanceMethodsAroundInterceptor {
    @Override
    public void beforeMethod(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes, MethodInterceptResult result) throws Throwable {
        ZoneAvoidanceRule rule = (ZoneAvoidanceRule) objInst;
        ILoadBalancer loadBalancer = rule.getLoadBalancer();
        List<Server> allServers = loadBalancer.getAllServers();
        if (allServers.isEmpty()) {
            result.defineReturnValue(null);
            return;
        }
        String env = (String) ServerContextHolder.get(ROUTE_LABEL);
        ServerSelector serverSelector = CloudServerSelector.build(env, allServers);
        List<Server> targetServers = serverSelector.selectServers();
        Server server = doChooseServer(targetServers, rule, allArguments);
        result.defineReturnValue(server);
    }

    private Server doChooseServer(List<Server> targetServers, ZoneAvoidanceRule rule, Object[] allArguments) {
        if (targetServers.isEmpty()) {
            return null;
        }
        Object loadBalancerKey = allArguments[0];
        Optional<Server> server = rule.getPredicate().chooseRoundRobinAfterFiltering(targetServers, loadBalancerKey);
        return server.isPresent() ? server.get() : null;
    }
}
