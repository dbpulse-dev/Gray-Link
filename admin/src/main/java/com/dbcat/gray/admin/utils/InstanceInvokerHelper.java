package com.dbcat.gray.admin.utils;


import com.dbcat.gray.admin.dto.AppInstanceResponse;
import com.dbcat.gray.admin.dto.RestResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.net.URI;


public abstract class InstanceInvokerHelper {

    protected static final Logger log = LoggerFactory.getLogger(InstanceInvokerHelper.class);

    private static RestTemplate restTemplate;


    static {
        SimpleClientHttpRequestFactory simpleClientHttpRequestFactory = new SimpleClientHttpRequestFactory();
        simpleClientHttpRequestFactory.setConnectTimeout(600);
        simpleClientHttpRequestFactory.setReadTimeout(10000);
        restTemplate = new RestTemplate(simpleClientHttpRequestFactory);
    }


    public static RestResult invokeRemote(AppInstanceResponse node, Object request, String api, String tip, int retry) {
        if (retry < 1) {
            return RestResult.buildFailure(tip + "失败");
        }
        retry--;
        String url = "http://" + node.getIp() + ":" + node.getAgentPort() + api;
        long st = System.currentTimeMillis();
        RestResult result = null;
        Throwable throwable = null;
        try {
            HttpHeaders headers = buildHeaders(node);
            RequestEntity requestEntity = new RequestEntity<>(request, headers, HttpMethod.POST, URI.create(url));
            ResponseEntity<RestResult> exchange = restTemplate.exchange(requestEntity, RestResult.class);
            result = exchange.getBody();
            return result;
        } catch (Throwable t) {
            log.error("{}失败:{},{},{}", tip, node.getAppName(), node.getIp(), t.getMessage());
            throwable = t;
            return invokeRemote(node, request, api, tip, retry);
        } finally {
            long timeCost = System.currentTimeMillis() - st;
            if (throwable != null) {
                log.warn("{}失败:{},{},耗时:{}", tip, node.getAppName(), node.getIp(), timeCost, throwable);
            } else {
                if (result.getCode() != 200) {
                    log.warn("{}失败:{},{},耗时:{},{}", tip, node.getAppName(), node.getIp(), timeCost, result.getMessage());
                } else {
                    log.info("{}成功:{},{},耗时:{}", tip, node.getAppName(), node.getIp(), timeCost);
                }
            }
        }
    }

    public static <T> RestResult<T> invokeRemote(AppInstanceResponse node, Object request, String api, String tip, int retry, ParameterizedTypeReference<RestResult<T>> responseType) {
        if (retry < 1) {
            return RestResult.buildFailure(tip + "失败");
        }
        retry--;
        String url = "http://" + node.getIp() + ":" + node.getAgentPort() + api;
        long st = System.currentTimeMillis();
        RestResult result = null;
        Throwable throwable = null;
        try {
            HttpHeaders headers = buildHeaders(node);
            RequestEntity requestEntity = new RequestEntity<>(request, headers, HttpMethod.POST, URI.create(url));
            ResponseEntity<RestResult<T>> exchange = restTemplate.exchange(requestEntity, responseType);
            result = exchange.getBody();
            return result;
        } catch (Throwable t) {
            log.error("{}失败:{},{},{}", tip, node.getAppName(), node.getIp(), t.getMessage());
            throwable = t;
            return invokeRemote(node, request, api, tip, retry);
        } finally {
            long timeCost = System.currentTimeMillis() - st;
            if (throwable != null) {
                log.warn("{}失败:{},{},耗时:{}", tip, node.getAppName(), node.getIp(), timeCost, throwable);
            } else {
                if (result.getCode() != 200) {
                    log.warn("{}失败:{},{},耗时:{},{}", tip, node.getAppName(), node.getIp(), timeCost, result.getMessage());
                } else {
                    log.info("{}成功:{},{},耗时:{}", tip, node.getAppName(), node.getIp(), timeCost);
                }
            }
        }
    }

    private static HttpHeaders buildHeaders(AppInstanceResponse instance) {
        HttpHeaders headers = new HttpHeaders();
        long timestamp = System.currentTimeMillis();
        headers.set("uuid", instance.getUuid());
        headers.set("timestamp", timestamp + "");
        return headers;
    }

}
