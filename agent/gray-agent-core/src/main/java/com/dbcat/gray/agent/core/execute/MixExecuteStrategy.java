package com.dbcat.gray.agent.core.execute;


import com.dbcat.gray.agent.core.dto.ExecuteResponse;
import com.dbcat.gray.agent.core.mq.MQConnection;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * 2混合模式(快的用同步，慢的用异步)
 */
public class MixExecuteStrategy implements ExecuteStrategy {


    @Override
    public boolean support(int strategy) {
        return strategy == 2;
    }

    @Override
    public List<ExecuteResponse> execute(List<MQConnection> connections, Consumer<MQConnection> consumer, String name) throws Exception {
        List<ExecuteResponse> responses = new ArrayList<>();
        for (MQConnection c : connections) {
            ExecuteType executeType = buildExecuteType(c.isFast(), name);
            ExecuteResponse rs = this.doExecute(c, executeType, consumer);
            responses.add(rs);
        }
        return responses;
    }


}
