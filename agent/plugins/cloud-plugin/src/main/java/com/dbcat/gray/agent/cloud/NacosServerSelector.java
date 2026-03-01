package com.dbcat.gray.agent.cloud;

import com.alibaba.cloud.nacos.ribbon.NacosServer;
import com.dbcat.gray.agent.core.server.AbstractServerSelector;
import com.netflix.loadbalancer.Server;

import java.util.List;

import static com.dbcat.gray.agent.core.conf.GrayConstant.INSTANCE_UUID;

/**
 * @author Blackfost
 */
public class NacosServerSelector extends AbstractServerSelector<Server> {

    private NacosServerSelector(String env, List<Server> servers) {
        super(env, servers);
    }

    public static NacosServerSelector build(String env, List<Server> servers) {
        return new NacosServerSelector(env, servers);
    }

    @Override
    protected String getInstanceUuid(Server server) {
        if (server instanceof NacosServer) {
            NacosServer nacosServer = (NacosServer) server;
            return nacosServer.getMetadata().get(INSTANCE_UUID);
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
        return servers.get(0) instanceof NacosServer;
    }
}
