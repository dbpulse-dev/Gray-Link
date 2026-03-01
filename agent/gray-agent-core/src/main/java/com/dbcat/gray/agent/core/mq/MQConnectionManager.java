package com.dbcat.gray.agent.core.mq;


import com.dbcat.gray.agent.core.dto.ExecuteResponse;
import com.dbcat.gray.agent.core.execute.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class MQConnectionManager {

    private volatile static List<MQConnection> connectionList = new ArrayList<>();


    private static List<ExecuteStrategy> executeStrategies = new ArrayList<>();

    static {
        executeStrategies.add(new SyncExecuteStrategy());
        executeStrategies.add(new AsyncExecuteStrategy());
        executeStrategies.add(new MixExecuteStrategy());
        executeStrategies.add(new MixConcurrentExecuteStrategy());
    }

    /**
     * @param connection
     * @return 添加成功返回true, 已存在返回false
     */
    public static boolean add(MQConnection connection) {
        connectionList = filterValid();
        for (MQConnection c : connectionList) {
            if (c.equals(connection)) {
                return false;
            }
        }
        connectionList.add(connection);
        return true;
    }

    public static boolean remove(MQConnection connection) {
        connectionList.remove(connection);
        return true;
    }


    /**
     * rocket mq 销毁重启,会创建新producer ，connection的producer会增多，需要移除
     * rabbitmq 重启很快切换可以同步，rocketmq 重启很慢（可能10多秒）建议用异步方式
     *
     * @param strategy 0同步方式，1异步方式，2混合模式(快的用同步，慢的用异步)，3混合同步并发(快的同步，慢的并发同步)
     * @return
     * @throws Exception
     */
    public static synchronized List<ExecuteResponse> restartAll(int strategy) throws Exception {
        return restartAll(strategy, () -> filterValid());
    }

    public static synchronized List<ExecuteResponse> onSubscriptChange(int strategy) throws Exception {
        return onSubscriptChange(strategy, () -> filterValid());
    }

    private static synchronized List<ExecuteResponse> onSubscriptChange(int strategy, Supplier<List<MQConnection>> sp) throws Exception {
        List<MQConnection> connections = sp.get();
        for (ExecuteStrategy executeStrategy : executeStrategies) {
            if (executeStrategy.support(strategy)) {
                return executeStrategy.execute(connections, connection -> {
                    connection.onSubscriptChange();
                }, "MQ订阅变更");
            }
        }
        return new ArrayList<>(0);
    }

    private static synchronized List<ExecuteResponse> restartAll(int strategy, Supplier<List<MQConnection>> sp) throws Exception {
        List<MQConnection> connections = sp.get();
        for (ExecuteStrategy executeStrategy : executeStrategies) {
            if (executeStrategy.support(strategy)) {
                return executeStrategy.execute(connections, connection -> {
                    connection.restart();
                }, "重置MQ连接");
            }
        }
        return new ArrayList<>(0);
    }

    public static List<MQConnection> filterValid() {
        return connectionList.stream().filter(c -> c.shouldReset()).sorted((c1, c2) -> {
            if (c1.order() < c2.order()) {
                return -1;
            }
            if (c1.order() > c2.order()) {
                return 1;
            }
            return 0;
        }).collect(Collectors.toList());
    }
}
