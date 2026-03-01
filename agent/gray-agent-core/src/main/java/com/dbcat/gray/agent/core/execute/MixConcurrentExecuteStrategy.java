package com.dbcat.gray.agent.core.execute;


import com.dbcat.gray.agent.core.dto.ExecuteResponse;
import com.dbcat.gray.agent.core.logging.api.ILog;
import com.dbcat.gray.agent.core.logging.api.LogManager;
import com.dbcat.gray.agent.core.mq.MQConnection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 3混合同步并发(快的同步，慢的并发同步)
 */
public class MixConcurrentExecuteStrategy implements ExecuteStrategy {

    private static final ILog logger = LogManager.getLogger(MixConcurrentExecuteStrategy.class);
    private static ThreadPoolExecutor executor = new ThreadPoolExecutor(4, 4, 30, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10),
            r -> new Thread(null, r, "mq-change-", 0L),
            (runnable, threadPoolExecutor) -> {
                logger.info("mq 切换的线程池满了");
                runnable.run();
            });

    @Override
    public boolean support(int strategy) {
        return strategy == 3;
    }

    @Override
    public List<ExecuteResponse> execute(List<MQConnection> connections, Consumer<MQConnection> consumer, String name) throws Exception {
        List<MQConnection> fastConnections = connections.stream().filter(c -> c.isFast()).collect(Collectors.toList());
        List<MQConnection> slowConnections = connections.stream().filter(c -> !c.isFast()).collect(Collectors.toList());
        List<ExecuteResponse> responses = executeFastConnections(fastConnections, consumer, name);
        List<ExecuteResponse> executeResponses = executeSlowConnections(slowConnections, consumer, name);
        responses.addAll(executeResponses);
        return responses;
    }


    /**
     * 同步执行耗时较快的mq
     *
     * @param connections
     * @param consumer
     * @param name
     * @return
     * @throws Exception
     */
    private List<ExecuteResponse> executeFastConnections(List<MQConnection> connections, Consumer<MQConnection> consumer, String name) throws Exception {
        List<ExecuteResponse> responses = new ArrayList<>();
        for (MQConnection c : connections) {
            ExecuteType executeType = buildExecuteType(c.isFast(), name);
            ExecuteResponse rs = this.doExecute(c, executeType, consumer);
            responses.add(rs);
        }
        return responses;
    }


    /**
     * 并发执行较为耗时的mq
     *
     * @param connections
     * @param consumer
     * @param name
     * @return
     * @throws Exception
     */
    private List<ExecuteResponse> executeSlowConnections(List<MQConnection> connections, Consumer<MQConnection> consumer, String name) throws Exception {
        List<ExecuteResponse> resultList = Collections.synchronizedList(new ArrayList(connections.size()));
        CountDownLatch countDownLatch = new CountDownLatch(connections.size());
        for (MQConnection c : connections) {
            Runnable task = () -> {
                ExecuteResponse rs = buildResponse(c);
                long l = System.currentTimeMillis();
                try {
                    ExecuteType executeType = buildExecuteType(true, name);
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
                    rs.setName(c.description());
                    long timeCost = System.currentTimeMillis() - l;
                    rs.setTimeCost(timeCost);
                    resultList.add(rs);
                    countDownLatch.countDown();
                }
            };
            executor.execute(task);
        }
        countDownLatch.await();
        return resultList;
    }

}
