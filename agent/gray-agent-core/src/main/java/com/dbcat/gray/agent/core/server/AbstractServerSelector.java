package com.dbcat.gray.agent.core.server;


import com.dbcat.gray.agent.core.conf.EnvStatus;
import com.dbcat.gray.agent.core.conf.GrayConstant;
import com.dbcat.gray.agent.core.dto.Instance;
import com.dbcat.gray.agent.core.logging.api.ILog;
import com.dbcat.gray.agent.core.logging.api.LogManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


/**
 * @param <S>
 * @author Blackfost
 */
public abstract class AbstractServerSelector<S> implements ServerSelector<S> {

    private static final ILog logger = LogManager.getLogger(AbstractServerSelector.class);

    private String env;

    protected List<S> servers;

    protected AbstractServerSelector(String env, List<S> servers) {
        this.env = env;
        this.servers = servers;
    }

    @Override
    public List<S> selectServers() {
        if (!serverTypeMatch()) {
            return null;
        }
        String serverName = this.getServerName();
        if (serverName == null) {
            logger.warn("灰度路由获取不到远程服务名称");
            return servers;
        }
        serverName = serverName.toLowerCase();
        List<S> targetServers = null;
        if (env == null) {
            logger.debug("调用正常的服务{}", serverName);
            targetServers = choseSevers(serverName, EnvStatus.NORMAL.getCode());
        } else {
            logger.debug("调用灰度的服务{}", serverName);
            targetServers = choseSevers(serverName, EnvStatus.GRAY.getCode());
        }
        if (targetServers.isEmpty()) {
            logger.info("没获取到相应状态{}的实例,所有实例信息{}", env, serverName);
            targetServers = servers;
        }
        return targetServers;
    }

    protected boolean serverTypeMatch() {
        return true;
    }

    /**
     * @param envStatus {@link GrayConstant}
     * @return
     */
    protected List<S> choseSevers(String serverName, int envStatus) {
        //通过服务名，相应状态的实列列表
        Map<String, Instance> instancesMap = ServerInstanceManager.getInstances(serverName, envStatus);
        if (instancesMap.isEmpty()) {
            return Collections.emptyList();
        }
        //通过实例uuid匹配相应的状态服务实例
        List<S> targetList = new ArrayList<>();
        for (S s : servers) {
            if (match(s, instancesMap)) {
                targetList.add(s);
            }
        }
        return targetList;
    }

    private boolean match(S s, Map<String, Instance> instancesMap) {
        String instanceUuid = getInstanceUuid(s);
        return instancesMap.containsKey(instanceUuid);
    }

    protected abstract String getInstanceUuid(S server);


    protected abstract String getServerName();
}
