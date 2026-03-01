package com.dbcat.gray.agent.cloud;

import com.dbcat.gray.agent.core.server.ServerSelector;
import com.netflix.loadbalancer.Server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Blackfost
 */
public class CloudServerSelector implements ServerSelector<Server> {

    private List<ServerSelector> selectors = new ArrayList<>(2);

    private static volatile boolean init;

    private static volatile boolean ribbonPresent;

    private static volatile boolean nacosPresent;

    static {
        ribbonPresent = isPresent("com.netflix.niws.loadbalancer.DiscoveryEnabledServer");
        nacosPresent = isPresent("com.alibaba.cloud.nacos.ribbon.NacosServer");
    }

    private CloudServerSelector(String env, List<Server> servers) {
        if (ribbonPresent) {
            selectors.add(RibbonServerSelector.build(env, servers));
        }
        if (nacosPresent) {
            selectors.add(NacosServerSelector.build(env, servers));
        }
    }

    public static CloudServerSelector build(String env, List<Server> servers) {
        return new CloudServerSelector(env, servers);
    }

    @Override
    public List<Server> selectServers() {
        for (ServerSelector selector : selectors) {
            List<Server> servers = selector.selectServers();
            if (servers.size() > 0) {
                return servers;
            }
        }
        return Collections.emptyList();
    }

    private static boolean isPresent(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
        }
        return false;
    }

}
