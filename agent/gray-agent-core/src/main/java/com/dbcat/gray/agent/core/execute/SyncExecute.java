package com.dbcat.gray.agent.core.execute;


import com.dbcat.gray.agent.core.logging.api.ILog;
import com.dbcat.gray.agent.core.logging.api.LogManager;

/**
 * 同步方式重置
 */
public class SyncExecute extends AbstractExecuteType {
    private static final ILog logger = LogManager.getLogger(SyncExecute.class);

    @Override
    public void execute(Execution execution) throws Exception {
        logger.info(String.format("正在%s %s...", name, execution.description()));
        long l = System.currentTimeMillis();
        try {
            execution.execute();
            logger.info(String.format("%s %s成功", name, execution.description()));
        } catch (Throwable t) {
            logger.error(String.format("%s  %s异常", name, execution.description()), t);
            throw t;
        } finally {
            logger.info(String.format("%s %s耗时%d", name, execution.description(), (System.currentTimeMillis() - l)));
        }
    }

}
