package com.dbcat.gray.admin;

import com.dbcat.gray.admin.dto.rabbit.*;
import com.dbcat.gray.admin.entity.RabbitMqServerEntity;
import com.dbcat.gray.admin.utils.RabbitMqApi;
import com.dbcat.gray.admin.utils.SignatureUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


public class RabbitMqApiTest /** extends TestBase **/
{
    protected static final Logger logger = LoggerFactory.getLogger(RabbitMqApi.class);
    private RabbitMqServerEntity serverEntity = this.getById(1);

    @Test
    public void getUsers() throws InterruptedException, UnsupportedEncodingException {
        RabbitMqApi api = RabbitMqApi.build(serverEntity);
        List<? extends RabbitUser> users = api.getUsers();
        assert users.size() > 0;
    }

    private RabbitMqServerEntity getById(Integer id) {
        RabbitMqServerEntity serverEntity = new RabbitMqServerEntity();
        serverEntity.setHost("192.168.0.105");
        serverEntity.setPort(5672);
        serverEntity.setManagerPort(15672);
        serverEntity.setUsername("admin");
        serverEntity.setPassword("admin123");
        return serverEntity;
    }

    @Test
    public void getPermissions() {
        RabbitMqApi api = RabbitMqApi.build(serverEntity);
        List<RabbitUserPermission> users = api.getPermissions();
        assert users.size() > 0;
    }

    @Test
    public void getPermissionsByVh() {
        RabbitMqServerEntity serverEntity = this.getById(1);
        RabbitMqApi api = RabbitMqApi.build(serverEntity);
        List<RabbitUserPermission> users = api.getPermissions("/audit.log-uat");
        assert users.size() > 0;
    }

    @Test
    public void createExchange() {

        RabbitMqApi build = RabbitMqApi.build(serverEntity);
        build.copyExchanges("/product-dev", "/product-dev");

    }

    @Test
    public void createVirtualHostConfigs() {
        RabbitMqApi api = RabbitMqApi.build(serverEntity);
        String srcVirtualHost = "/product-dev";
        String grayVirtualHost = srcVirtualHost + "-gray";
        RabbitVirtualHost srcVirtualHostConfig = api.getVirtualHost(srcVirtualHost);
        //1.创建灰度virtualhost
        api.createVirtualHost(grayVirtualHost, srcVirtualHostConfig.getDescription(), srcVirtualHostConfig.getTags());
        //2.copy相同的用户授权，给用户授权，应用所配置的用户才能访问
        List<RabbitUserPermission> permissions = api.getPermissions(srcVirtualHost);
        for (RabbitUserPermission user : permissions) {
            api.setPermissions(grayVirtualHost, user);
        }
        //3.创建exchange
        RabbitOperateResult result = api.copyExchanges(srcVirtualHost, grayVirtualHost);
        logger.info("创建exchagne数:{},成功数:{}", result.getTotalCount(), result.getSuccessCount());
        //4.创建queues
        RabbitOperateResult result2 = api.copyQueues(srcVirtualHost, grayVirtualHost);
        logger.info("创建队列数:{},成功数:{}", result2.getTotalCount(), result2.getSuccessCount());
        //5 绑定queues
        RabbitOperateResult result3 = api.bindingExchanges(srcVirtualHost, grayVirtualHost);
        logger.info("绑定队列数:{},成功数:{}", result3.getTotalCount(), result3.getSuccessCount());

    }

    @Test
    public void getExchangesByVh() {
        RabbitMqServerEntity serverEntity = this.getById(1);
        RabbitMqApi build = RabbitMqApi.build(serverEntity);
        build.getExchangesByVh("/apple-dev");

    }

    @Test
    public void getOverView() {
        RabbitMqServerEntity serverEntity = this.getById(1);
        RabbitMqApi api = RabbitMqApi.build(serverEntity);
        OverView overView = api.getOverView();
    }

    @Test
    public void getNodes() {
        RabbitMqServerEntity serverEntity = this.getById(1);
        RabbitMqApi api = RabbitMqApi.build(serverEntity);
        List<RabbitClusterNode> nodes = api.getNodes();
        assert nodes.size() > 0;
    }


    @Test
    public void getNodesMD5() {
        RabbitMqServerEntity serverEntity = this.getById(1);
        RabbitMqApi api = RabbitMqApi.build(serverEntity);
        List<RabbitClusterNode> nodes = api.getNodes();
        assert nodes.size() > 0;
        final String s = buildClusterUuid(nodes, serverEntity.getHost());
        assert s != null;
    }

    @Test
    public void getVhs() throws InterruptedException, UnsupportedEncodingException {
        RabbitMqServerEntity serverEntity = this.getById(1);
        RabbitMqApi api = RabbitMqApi.build(serverEntity);
        RabbitVirtualHost virtualHost = api.getVirtualHost("/th-scrm");
        assert virtualHost != null;
        final List<RabbitVirtualHost> virtualHosts = api.getVirtualHosts();
        assert virtualHosts.size() > 0;
        for (RabbitVirtualHost vh : virtualHosts) {
            List<RabbitQueue> queuesByVh = api.getQueuesByVh(vh.getName());
            System.out.println("qu");
        }

    }


    private String buildClusterUuid(List<RabbitClusterNode> nodes, String host) {
        Set<String> ips = new HashSet<>();
        for (RabbitClusterNode node : nodes) {
            List<LinkNode> linkNodes = node.getClusterLinks();
            for (LinkNode ln : linkNodes) {
                ips.add(ln.getPeerAddr());
            }
        }
        if (ips.isEmpty()) {
            return SignatureUtils.createMd5(host);
        }
        String collect = ips.stream().sorted().collect(Collectors.joining(","));
        return SignatureUtils.createMd5(collect);
    }
}
