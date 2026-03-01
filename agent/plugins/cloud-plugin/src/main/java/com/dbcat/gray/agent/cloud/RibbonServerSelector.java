package com.dbcat.gray.agent.cloud;

import com.dbcat.gray.agent.core.conf.GrayConstant;
import com.dbcat.gray.agent.core.server.AbstractServerSelector;
import com.netflix.loadbalancer.Server;
import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;

import java.util.List;
import java.util.Map;

/**
 * @author Blackfost
 */
public class RibbonServerSelector extends AbstractServerSelector<Server> {

    private RibbonServerSelector(String env, List<Server> servers) {
        super(env, servers);
    }

    public static RibbonServerSelector build(String env, List<Server> servers) {
        return new RibbonServerSelector(env, servers);
    }

    @Override
    protected String getInstanceUuid(Server server) {
        if (server instanceof DiscoveryEnabledServer) {
            DiscoveryEnabledServer eurekaServer = (DiscoveryEnabledServer) server;
            Map<String, String> metadata = eurekaServer.getInstanceInfo().getMetadata();
            return metadata.get(GrayConstant.INSTANCE_UUID);
        }
        return null;
    }

    @Override
    protected String getServerName() {
        Server server = servers.get(0);
        Server.MetaInfo metaInfo = server.getMetaInfo();
        return metaInfo.getAppName();
    }

    @Override
    protected boolean serverTypeMatch() {
        return servers.get(0) instanceof DiscoveryEnabledServer;
    }
}
