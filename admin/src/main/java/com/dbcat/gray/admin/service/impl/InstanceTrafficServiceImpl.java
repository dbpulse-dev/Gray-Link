package com.dbcat.gray.admin.service.impl;

import com.dbcat.gray.admin.dto.Counter;
import com.dbcat.gray.admin.dto.InstanceTraffic;
import com.dbcat.gray.admin.dto.Pair;
import com.dbcat.gray.admin.entity.InstanceTrafficEntity;
import com.dbcat.gray.admin.mapper.InstanceTrafficMapper;
import com.dbcat.gray.admin.service.InstanceTrafficService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class InstanceTrafficServiceImpl implements InstanceTrafficService, Runnable {

    protected static final Logger logger = LoggerFactory.getLogger(InstanceTrafficServiceImpl.class);
    protected BlockingQueue<InstanceTraffic> dataQueue = new LinkedBlockingQueue(1000);

    @Autowired
    private InstanceTrafficMapper trafficMapper;

    @Autowired
    private DataSource dataSource;

    @PostConstruct
    public void init() {
        new Thread(this, "traffic-save").start();
    }

    @Override
    public void save(InstanceTraffic report) {
        dataQueue.offer(report);
    }

    @Override
    public void run() {
        while (true) {
            try {
                InstanceTraffic data = dataQueue.poll();
                if (data == null) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(100);
                    } catch (Exception e) {
                    }
                    continue;
                }
                List<InstanceTrafficEntity> newEntities = buildEntities(data);
                List<InstanceTrafficEntity> existEntities = trafficMapper.listByUuid(data.getUuid());
                if (existEntities.isEmpty()) {
                    batchInsert(newEntities);
                    continue;
                }
                Pair<List<InstanceTrafficEntity>, List<InstanceTrafficEntity>> addOrUpdateEntities = findAddOrUpdateEntities(newEntities, existEntities);
                List<InstanceTrafficEntity> addEntities = addOrUpdateEntities.getKey();
                if (!addEntities.isEmpty()) {
                    batchInsert(addEntities);
                }
                List<InstanceTrafficEntity> updateEntities = addOrUpdateEntities.getValue();
                if (!updateEntities.isEmpty()) {
                    trafficMapper.batchUpdate(updateEntities);
                }
                trafficMapper.setUpdate(data.getUuid());
            } catch (Throwable t) {
                logger.error("保存数据失败异常", t);
            }
        }
    }


    private Pair<List<InstanceTrafficEntity>, List<InstanceTrafficEntity>> findAddOrUpdateEntities(List<InstanceTrafficEntity> newEntities, List<InstanceTrafficEntity> existEntities) {
        Map<String, InstanceTrafficEntity> existMap = existEntities.stream().collect(Collectors.toMap(e -> e.getUuid() + e.getName() + e.getDirection() + e.getType(), Function.identity(), (k1, k2) -> k1));
        List<InstanceTrafficEntity> updateEntities = new ArrayList<>();
        List<InstanceTrafficEntity> addEntities = new ArrayList<>();
        for (InstanceTrafficEntity e : newEntities) {
            String key = e.getUuid() + e.getName() + e.getDirection() + e.getType();
            InstanceTrafficEntity existEntity = existMap.get(key);
            if (existEntity == null) {
                addEntities.add(e);
                continue;
            }
            if (existEntity.getValue() == e.getValue()) {
                continue;
            }
            e.setId(existEntity.getId());
            updateEntities.add(e);
        }
        return new Pair<>(addEntities, updateEntities);

    }


    private List<InstanceTrafficEntity> buildEntities(InstanceTraffic data) {
        List<InstanceTrafficEntity> produce = buildEntities(data, "produce");
        List<InstanceTrafficEntity> consume = buildEntities(data, "consume");
        consume.addAll(produce);
        return consume;
    }


    private List<InstanceTrafficEntity> buildEntities(InstanceTraffic traffic, String direction) {
        Map<String, List<Counter>> metrics = traffic.getMetrics();
        List<Counter> counters = metrics.get(direction);
        List<InstanceTrafficEntity> entities = new ArrayList<>();
        counters.forEach(counter -> {
            if (counter.getValue() == 0) {
                return;
            }
            InstanceTrafficEntity entity = new InstanceTrafficEntity();
            entity.setAppName(traffic.getAppName());
            entity.setUuid(traffic.getUuid());
            entity.setDirection(direction);
            entity.setType(counter.getType());
            entity.setName(counter.getName());
            entity.setValue(counter.getValue());
            entities.add(entity);
        });
        return entities;
    }


    public synchronized void batchInsert(List<InstanceTrafficEntity> entities) {
        String BATCH_INSERT_SQL = "INSERT INTO instance_traffic (app_name, uuid,type,name,value,direction) VALUES (?,?,?,?,?,?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(BATCH_INSERT_SQL)) {
            connection.setAutoCommit(false);
            for (InstanceTrafficEntity item : entities) {
                ps.setString(1, item.getAppName());
                ps.setString(2, item.getUuid());
                ps.setInt(3, item.getType());
                ps.setString(4, item.getName());
                ps.setLong(5, item.getValue());
                ps.setString(6, item.getDirection());
                ps.addBatch();
            }
            ps.executeBatch(); // 执行剩余记录
            connection.commit();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
