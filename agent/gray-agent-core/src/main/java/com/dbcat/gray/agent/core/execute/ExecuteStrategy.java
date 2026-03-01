package com.dbcat.gray.agent.core.execute;


import com.dbcat.gray.agent.core.dto.ExecuteResponse;
import com.dbcat.gray.agent.core.logging.api.ILog;
import com.dbcat.gray.agent.core.logging.api.LogManager;
import com.dbcat.gray.agent.core.mq.MQConnection;

import java.util.List;
import java.util.function.Consumer;

/**
 * strategy 0同步方式，1异步方式，2混合模式(快的用同步，慢的用异步)，3混合同步并发(快的同步，慢的并发同步)
 */
public interface ExecuteStrategy {

    ILog logger = LogManager.getLogger(ExecuteStrategy.class);

    boolean support(int strategy);

    List<ExecuteResponse> execute(List<MQConnection> connections, Consumer<MQConnection> consumer, String name) throws Exception;


    default ExecuteType buildExecuteType(boolean sync, String name) {
        SyncExecute syncMQReset = new SyncExecute();
        syncMQReset.setName(name);
        AsyncExecute asyncReset = new AsyncExecute(syncMQReset);
        asyncReset.setName(name);
        return sync ? syncMQReset : asyncReset;
    }

    default ExecuteResponse doExecute(MQConnection c, ExecuteType executeType, Consumer<MQConnection> consumer) throws Exception {
        long l = System.currentTimeMillis();
        ExecuteResponse rs = buildResponse(c);
        try {
            executeType.execute(new Execution() {
                @Override
                public void execute() {
                    consumer.accept(c);
                }

                @Override
                public String description() {
                    return c.description();
                }
            });
        } catch (Throwable t) {
            logger.error("执行mq订阅调整异常", t);
            rs.setCode(500);
            rs.setMessage(t.getMessage());
        } finally {
            long timeCost = System.currentTimeMillis() - l;
            rs.setTimeCost(timeCost);
        }
        return rs;
    }


    default ExecuteResponse buildResponse(MQConnection c) {
        ExecuteResponse rs = new ExecuteResponse();
        rs.setName(c.description());
        rs.setType(c.getType());
        return rs;
    }
}
