package com.dbcat.gray.agent.core.execute;


import com.dbcat.gray.agent.core.dto.ExecuteResponse;
import com.dbcat.gray.agent.core.mq.MQConnection;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * 1异步方式
 */
public class AsyncExecuteStrategy implements ExecuteStrategy {


    @Override
    public boolean support(int strategy) {
        return strategy == 1;
    }


    @Override
    public List<ExecuteResponse> execute(List<MQConnection> connections, Consumer<MQConnection> consumer, String name) throws Exception {
        List<ExecuteResponse> responses = new ArrayList<>();
        for (MQConnection c : connections) {
            ExecuteType executeType = buildExecuteType(false, name);
            ExecuteResponse rs = this.doExecute(c, executeType, consumer);
            responses.add(rs);
        }
        return responses;
    }

}
