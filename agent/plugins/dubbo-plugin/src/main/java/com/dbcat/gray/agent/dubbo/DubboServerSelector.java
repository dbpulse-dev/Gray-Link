package com.dbcat.gray.agent.dubbo;

import com.dbcat.gray.agent.core.server.AbstractServerSelector;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.Invoker;

import java.util.List;

import static com.dbcat.gray.agent.core.conf.GrayConstant.INSTANCE_UUID;

/**
 * @author Blackfost
 */
public class DubboServerSelector extends AbstractServerSelector<Invoker> {

    private DubboServerSelector(String env, List<Invoker> servers) {
        super(env, servers);
    }

    public static DubboServerSelector build(String env, List<Invoker> servers) {
        return new DubboServerSelector(env, servers);
    }

    @Override
    protected String getInstanceUuid(Invoker server) {
        return server.getUrl().getParameter(INSTANCE_UUID);
    }

    @Override
    protected String getServerName() {
        Invoker invoker = servers.get(0);
        URL providerUrl = invoker.getUrl();
        return providerUrl.getParameter("remote.application");
    }
}
