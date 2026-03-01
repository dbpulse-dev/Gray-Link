package com.dbcat.gray.agent.core.server.endpoint;

import com.dbcat.gray.agent.core.dto.Instance;
import com.dbcat.gray.agent.core.server.RestResult;
import com.dbcat.gray.agent.core.server.ServerInstanceManager;

import java.util.List;

/**
 * 通知实例变化
 *
 * @author Blackfost
 */
public class InstanceChangeNotifyEndpoint implements Endpoint<List<Instance>, Void> {


    @Override
    public String path() {
        return "/gray/instance/change";
    }


    @Override
    public RestResult invoke(List<Instance> instances) {
        if (instances == null) {
            return RestResult.buildSuccess();
        }
        ServerInstanceManager.onInstanceChange(instances);
        return RestResult.buildSuccess();
    }

}
