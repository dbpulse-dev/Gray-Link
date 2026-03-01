package com.dbcat.gray.agent.core.execute;


import com.dbcat.gray.agent.core.logging.api.ILog;
import com.dbcat.gray.agent.core.logging.api.LogManager;

/**
 * 异步方式重置
 */
public class AsyncExecute extends AbstractExecuteType {

    private static final ILog logger = LogManager.getLogger(AsyncExecute.class);

    private SyncExecute executeType;

    public AsyncExecute(SyncExecute executeType) {
        this.executeType = executeType;
    }

    @Override
    public void execute(Execution execution) {
        logger.info(String.format("正在异步 %s %s...", name, execution.description()));
        new Thread("mq-connect-change") {
            @Override
            public void run() {
                try {
                    executeType.execute(execution);
                } catch (Throwable e) {
                    logger.error(String.format("%s %s异常", name, execution.description()), e);
                }
            }
        }.start();
    }
}
