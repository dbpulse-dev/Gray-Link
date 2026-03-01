package com.dbcat.gray.admin.utils;


import com.dbcat.gray.admin.dto.rabbit.*;
import com.dbcat.gray.admin.entity.RabbitMqServerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.dbcat.gray.admin.utils.VersionComparator.less;

public class RabbitMqApi {

    protected static final Logger logger = LoggerFactory.getLogger(RabbitMqApi.class);
    private String address;
    private String username;
    private String password;

    private String authorization;

    private RestTemplate restTemplate;

    private OverView overView;

    public static RabbitMqApi build(RabbitMqServerEntity serverEntity) {
        RabbitMqApi api = new RabbitMqApi();
        api.restTemplate = buildRestTemplate();
        api.username = serverEntity.getUsername();
        api.password = serverEntity.getPassword();
        api.address = "http://" + serverEntity.getHost() + ":" + serverEntity.getManagerPort();
        String rabbitAuthorization = api.username + ":" + api.password;
        api.authorization = "Basic " + Base64.getEncoder().encodeToString(rabbitAuthorization.getBytes());
        return api;
    }


    private static RestTemplate buildRestTemplate() {
        SimpleClientHttpRequestFactory simpleClientHttpRequestFactory = new SimpleClientHttpRequestFactory();
        simpleClientHttpRequestFactory.setConnectTimeout(500);
        simpleClientHttpRequestFactory.setReadTimeout(60000);
        return new RestTemplate(simpleClientHttpRequestFactory);
    }

    public List<RabbitClusterNode> getNodes() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", authorization);
        String url = address + "/api/nodes";
        RequestEntity entity = new RequestEntity<>(headers, HttpMethod.GET, URI.create(url));
        try {
            ResponseEntity<List<RabbitClusterNode>> exchange = restTemplate.exchange(entity,
                    new ParameterizedTypeReference<List<RabbitClusterNode>>() {
                    });
            return exchange.getBody();
        } catch (Throwable t) {
            logger.error("获取所节点失败:{},", address, t);
            throw new RuntimeException("获取所节点失败");
        }
    }

    public String getVersion() {
        return this.getOverView().getVersion();
    }

    public OverView getOverView() {
        if (overView != null) {
            return overView;
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", authorization);
        String url = address + "/api/overview";
        RequestEntity entity = new RequestEntity<>(headers, HttpMethod.GET, URI.create(url));
        try {
            ResponseEntity<OverView> exchange = restTemplate.exchange(entity,
                    new ParameterizedTypeReference<OverView>() {
                    });
            this.overView = exchange.getBody();
            return this.overView;
        } catch (Throwable t) {
            logger.error("获取Overview失败:{},", address, t);
            throw new RuntimeException("获取Overview失败");
        }
    }

    public List<RabbitVirtualHost> getVirtualHosts() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", authorization);
        String url = address + "/api/vhosts";
        RequestEntity entity = new RequestEntity<>(headers, HttpMethod.GET, URI.create(url));
        try {
            ResponseEntity<List<RabbitVirtualHost>> exchange = restTemplate.exchange(entity,
                    new ParameterizedTypeReference<List<RabbitVirtualHost>>() {
                    });
            return exchange.getBody();
        } catch (Throwable t) {
            logger.error("获取所有virtualHosts失败:{},", address, t);
            throw new RuntimeException("获取所virtualHosts失败");
        }

    }

    public RabbitVirtualHost getVirtualHost(String vh) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", authorization);
        try {
            String url = address + "/api/vhosts/" + URLEncoder.encode(vh, "utf-8");
            RequestEntity entity = new RequestEntity<>(headers, HttpMethod.GET, URI.create(url));
            ResponseEntity<RabbitVirtualHost> exchange = restTemplate.exchange(entity,
                    new ParameterizedTypeReference<RabbitVirtualHost>() {
                    });
            return exchange.getBody();
        } catch (Throwable t) {
            logger.error("获取virtualHost失败:{},", vh, t);
            throw new RuntimeException("获取virtualHost失败");
        }
    }

    /**
     * tags 用"," 分割
     *
     * @param vh
     * @param description
     * @param tags
     * @throws UnsupportedEncodingException
     */
    public void createVirtualHost(String vh, String description, List<String> tags) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", authorization);
        try {
            String url = address + "/api/vhosts/" + URLEncoder.encode(vh, "utf-8");
            Map<String, String> params = new HashMap<>();
            if (!StringUtils.isEmpty(description)) {
                params.put("description", description);
            }
            if (tags != null && tags.size() > 0) {
                String ts = tags.stream().collect(Collectors.joining(","));
                params.put("tags", ts);
            }
            RequestEntity entity = new RequestEntity<>(params, headers, HttpMethod.PUT, URI.create(url));
            ResponseEntity<Void> rs = restTemplate.exchange(entity, Void.class);
            if (rs.getStatusCodeValue() == 201) {
                logger.info("创建virtualHost成功{}", vh);
            } else {
                logger.info("virtualHost可能已存{},code:{}", vh, rs.getStatusCodeValue());
            }
        } catch (Throwable t) {
            logger.error("创建virtualHost失败:{},", vh, t);
            throw new RuntimeException("获取virtualHost失败");
        }
    }

    public List<? extends RabbitUser> getUsers() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", authorization);
        String url = address + "/api/users";

        if (less(getVersion(), "3.10.0")) {
            RequestEntity entity = new RequestEntity<>(headers, HttpMethod.GET, URI.create(url));
            ResponseEntity<List<RabbitUserV1>> exchange = restTemplate.exchange(entity,
                    new ParameterizedTypeReference<List<RabbitUserV1>>() {
                    });
            return exchange.getBody();
        } else {
            RequestEntity entity = new RequestEntity<>(headers, HttpMethod.GET, URI.create(url));
            ResponseEntity<List<RabbitUserV2>> exchange = restTemplate.exchange(entity,
                    new ParameterizedTypeReference<List<RabbitUserV2>>() {
                    });
            return exchange.getBody();
        }
    }


    public List<RabbitUserPermission> getPermissions(String vh) {
        List<RabbitUserPermission> permissions = getPermissions();
        return permissions.stream().filter(p -> vh.equals(p.getVhost())).collect(Collectors.toList());
    }

    public List<RabbitUserPermission> getPermissions() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", authorization);
        String url = address + "/api/permissions";
        RequestEntity entity = new RequestEntity<>(headers, HttpMethod.GET, URI.create(url));
        try {
            ResponseEntity<List<RabbitUserPermission>> exchange = restTemplate.exchange(entity,
                    new ParameterizedTypeReference<List<RabbitUserPermission>>() {
                    });
            return exchange.getBody();
        } catch (Throwable t) {
            logger.error("获取permissions失败:{},", address, t);
            throw new RuntimeException("获取permissions失败");
        }

    }

    /**
     * {
     * "username": "gray_test",
     * "vhost": "/airflow-dev",
     * "configure": ".*",
     * "write": ".*",
     * "read": ".*"
     * }
     *
     * @param vh
     * @throws UnsupportedEncodingException
     */
    public void setPermissions(String vh, RabbitUserPermission permission) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", authorization);
        try {
            String url = address + "/api/permissions/" + URLEncoder.encode(vh, "utf-8") + "/" + permission.getUser();
            Map<String, String> params = new HashMap<>();
            params.put("username", permission.getUser());
            params.put("vhost", vh);
            params.put("configure", permission.getConfigure());
            params.put("write", permission.getWrite());
            params.put("read", permission.getRead());
            RequestEntity entity = new RequestEntity<>(params, headers, HttpMethod.PUT, URI.create(url));
            ResponseEntity<Void> rs = restTemplate.exchange(entity, Void.class);
            if (rs.getStatusCodeValue() == 201) {
                logger.info("授权{}给{}成功", vh, username);
            } else {
                logger.info("授权{}给{}可能已存在,code:{}", vh, username, rs.getStatusCodeValue());
            }
        } catch (Throwable t) {
            logger.error("授权{}给{}失败:{},{},", address, vh, t);
            throw new RuntimeException("授权失败");
        }
    }


    /**
     * 获取virtualhost 的所有队列
     *
     * @param vh
     * @return
     * @throws UnsupportedEncodingException
     */
    public List<RabbitQueue> getQueuesByVh(String vh) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", authorization);
        try {
            String url = address + "/api/queues/" + URLEncoder.encode(vh, "utf-8");
            RequestEntity entity = new RequestEntity<>(headers, HttpMethod.GET, URI.create(url));
            ResponseEntity<List<RabbitQueue>> exchange = restTemplate.exchange(entity,
                    new ParameterizedTypeReference<List<RabbitQueue>>() {
                    });
            return exchange.getBody();
        } catch (Throwable t) {
            logger.error("获取队列失败:{},{},", address, vh, t);
            throw new RuntimeException("获取队列失败");
        }

    }


    /**
     * 获取virtualhost 的所有绑定信息
     *
     * @param vh
     * @return
     * @throws UnsupportedEncodingException
     */
    public List<RabbitQueueBinding> getBindingsByVh(String vh) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", authorization);
        try {
            String url = address + "/api/bindings/" + URLEncoder.encode(vh, "utf-8");
            RequestEntity entity = new RequestEntity<>(headers, HttpMethod.GET, URI.create(url));
            ResponseEntity<List<RabbitQueueBinding>> exchange = restTemplate.exchange(entity, new ParameterizedTypeReference<List<RabbitQueueBinding>>() {
            });
            return exchange.getBody();
        } catch (Throwable t) {
            logger.error("获取绑定信息失败:{},", vh, t);
            throw new RuntimeException("获取绑定信息失败");
        }
    }


    public List<RabbitExchange> getExchangesByVh(String vh) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", authorization);
        try {
            String url = address + "/api/exchanges/" + URLEncoder.encode(vh, "utf-8");
            RequestEntity entity = new RequestEntity<>(headers, HttpMethod.GET, URI.create(url));
            ResponseEntity<List<RabbitExchange>> exchange = restTemplate.exchange(entity, new ParameterizedTypeReference<List<RabbitExchange>>() {
            });
            return exchange.getBody();
        } catch (Throwable t) {
            logger.error("获取exchange信息失败:{},{},", address, vh, t);
            throw new RuntimeException("获取exchange信息失败");
        }
    }

    /**
     * 删除 指定vh的队列
     *
     * @param vh
     * @throws UnsupportedEncodingException
     */
    public RabbitOperateResult deleteQueuesByVh(String vh) {
        if (!vh.endsWith("gray")) {
            throw new RuntimeException("只能删除灰度virtualhost的queue");
        }

        List<RabbitQueue> queues = getQueuesByVh(vh);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", authorization);
        queues = queues.stream().filter(q -> !StringUtils.isEmpty(q.getName())).collect(Collectors.toList());
        RabbitOperateResult result = new RabbitOperateResult();
        result.setTotalCount(queues.size());
        int successCount = 0;
        for (RabbitQueue queue : queues) {
            try {
                String url = address + "/api/queues/" + URLEncoder.encode(vh, "utf-8") + "/" + URLEncoder.encode(queue.getName(), "utf-8");
                RequestEntity entity = new RequestEntity<>(queue, headers, HttpMethod.DELETE, URI.create(url));
                ResponseEntity<Void> rs = restTemplate.exchange(entity, Void.class);
                if (rs.getStatusCodeValue() == 204) {
                    logger.info("删除queue成功{}", queue.getName());
                    successCount++;
                } else {
                    logger.info("删除queue失败{},code:{}", queue.getName(), rs.getStatusCodeValue());
                }
            } catch (Throwable t) {
                logger.error("删除queue失败:{},", queue.getName(), t);
            }
        }
        result.setSuccessCount(successCount);
        return result;
    }


    public RabbitOperateResult deleteExchangesByVh(String vh) {
        if (!vh.endsWith("gray")) {
            throw new RuntimeException("只能删除灰度virtualhost的change");
        }
        List<RabbitExchange> exchanges = this.getExchangesByVh(vh);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", authorization);
        exchanges = exchanges.stream().filter(q -> !StringUtils.isEmpty(q.getName())).collect(Collectors.toList());
        RabbitOperateResult result = new RabbitOperateResult();
        result.setTotalCount(exchanges.size());
        int successCount = 0;
        for (RabbitExchange exchange : exchanges) {
            try {
                String url = address + "/api/exchanges/" + URLEncoder.encode(vh, "utf-8") + "/" + URLEncoder.encode(exchange.getName(), "utf-8");
                RequestEntity entity = new RequestEntity<>(exchange, headers, HttpMethod.DELETE, URI.create(url));
                ResponseEntity<Void> rs = restTemplate.exchange(entity, Void.class);
                if (rs.getStatusCodeValue() == 204) {
                    logger.info("删除exchange成功{}", exchange.getName());
                    successCount++;
                } else {
                    logger.info("删除exchange失败{},code:{}", exchange.getName(), rs.getStatusCodeValue());
                }
            } catch (Throwable t) {
                logger.error("删除exchange失败:{},", exchange.getName(), t);
            }
        }
        result.setSuccessCount(successCount);
        return result;
    }


    public RabbitOperateResult copyExchanges(String sourceVh, String targetVh) {
        if (!targetVh.endsWith("gray")) {
            throw new RuntimeException("只能创建灰度virtualhost的change");
        }
        List<RabbitExchange> exchanges = this.getExchangesByVh(sourceVh);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", authorization);
        exchanges = exchanges.stream().filter(q -> !StringUtils.isEmpty(q.getName())).collect(Collectors.toList());
        RabbitOperateResult result = new RabbitOperateResult();
        result.setTotalCount(exchanges.size());
        int successCount = 0;
        for (RabbitExchange exchange : exchanges) {
            try {
                String url = address + "/api/exchanges/" + URLEncoder.encode(targetVh, "utf-8") + "/" + URLEncoder.encode(exchange.getName(), "utf-8");
                RequestEntity entity = new RequestEntity<>(exchange, headers, HttpMethod.PUT, URI.create(url));
                ResponseEntity<Void> rs = restTemplate.exchange(entity, Void.class);
                if (rs.getStatusCodeValue() == 201) {
                    logger.info("创建exchange成功{}", exchange.getName());
                    successCount++;
                } else {
                    logger.info("创建exchange失败,可能已存在{},code:{}", exchange.getName(), rs.getStatusCodeValue());
                }
            } catch (Throwable t) {
                logger.error("创建exchange失败:{},", exchange.getName(), t);
            }
        }
        result.setSuccessCount(successCount);
        return result;
    }


    public RabbitOperateResult copyQueues(String sourceVh, String targetVh) {
        if (!targetVh.endsWith("gray")) {
            throw new RuntimeException("只能创建灰度virtualhost的queue");
        }
        List<RabbitQueue> queues = this.getQueuesByVh(sourceVh);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", authorization);
        queues = queues.stream().filter(q -> !StringUtils.isEmpty(q.getName())).collect(Collectors.toList());
        RabbitOperateResult result = new RabbitOperateResult();
        result.setTotalCount(queues.size());
        int successCount = 0;
        for (RabbitQueue queue : queues) {
            try {
                String url = address + "/api/queues/" + URLEncoder.encode(targetVh, "utf-8") + "/" + URLEncoder.encode(queue.getName(), "utf-8");
                RequestEntity entity = new RequestEntity<>(queue, headers, HttpMethod.PUT, URI.create(url));
                ResponseEntity<Void> rs = restTemplate.exchange(entity, Void.class);
                if (rs.getStatusCodeValue() == 201) {
                    logger.info("创建queue成功{}", queue.getName());
                    successCount++;
                } else {
                    logger.info("创建queue失败{},code:{}", queue.getName(), rs.getStatusCodeValue());
                }
            } catch (Throwable t) {
                logger.error("创建queue失败:{},", queue.getName(), t);
            }
        }
        result.setSuccessCount(successCount);
        return result;
    }


    public RabbitOperateResult bindingExchanges(String sourceVh, String targetVh) {
        if (!targetVh.endsWith("gray")) {
            throw new RuntimeException("只能绑定灰度virtualhost的queue");
        }
        List<RabbitQueueBinding> allBindings = this.getBindingsByVh(sourceVh);
        List<RabbitQueueBinding> bindings = allBindings.stream().filter(b -> !StringUtils.isEmpty(b.getExchange())).collect(Collectors.toList());
        RabbitOperateResult result = new RabbitOperateResult();
        result.setTotalCount(bindings.size());
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", authorization);
        int successCount = 0;
        for (RabbitQueueBinding binding : bindings) {
            try {
                String url = address + "/api/bindings/" + URLEncoder.encode(targetVh, "utf-8") + "/e/" + URLEncoder.encode(binding.getExchange(), "utf-8") + "/q/" + URLEncoder.encode(binding.getDestination(), "utf-8");
                RequestEntity entity = new RequestEntity<>(binding, headers, HttpMethod.POST, URI.create(url));
                ResponseEntity<Void> rs = restTemplate.exchange(entity, Void.class);
                if (rs.getStatusCodeValue() == 201) {
                    logger.info("绑定queue成功{}", binding.getDestination());
                    successCount++;
                } else {
                    logger.info("绑定queue失败,可能存在{},code:{}", binding.getDestination(), rs.getStatusCodeValue());
                }
            } catch (Throwable t) {
                logger.error("绑定queue失败:{},", binding.getDestination(), t);
            }
        }
        result.setSuccessCount(successCount);
        return result;
    }
}
