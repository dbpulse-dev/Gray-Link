package com.dbcat.gray.agent.core.server.endpoint;

import com.dbcat.gray.agent.core.dto.ExecuteResponse;
import com.dbcat.gray.agent.core.mq.MQConnection;
import com.dbcat.gray.agent.core.mq.MQConnectionManager;
import com.dbcat.gray.agent.core.server.RestResult;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Blackfost
 */
public class MQListEndpoint implements Endpoint<Void, List<ExecuteResponse>> {


    @Override
    public String path() {
        return "/mq/list";
    }


    @Override
    public RestResult<List<ExecuteResponse>> invoke(Void request) throws Exception {
        List<MQConnection> mqConnections = MQConnectionManager.filterValid();
        List<ExecuteResponse> responses = mqConnections.stream().map(c -> {
            ExecuteResponse response = new ExecuteResponse();
            response.setType(c.getType());
            response.setName(c.description());
            return response;
        }).collect(Collectors.toList());
        return RestResult.buildSuccess(responses);
    }

}
