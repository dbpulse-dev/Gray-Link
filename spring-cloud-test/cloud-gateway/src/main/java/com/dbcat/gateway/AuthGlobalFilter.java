package com.dbcat.gateway;

import com.dbcat.gateway.gray.Account;
import com.dbcat.gateway.gray.LoginMocker;
import com.dbcat.gray.filter.GrayFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    protected static final Logger logger = LoggerFactory.getLogger(AuthGlobalFilter.class);
    private ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private LoginMocker loginMocker;


    @Autowired
    private GrayFilter grayFilter;


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        Account account = getLoginAccount(exchange);
        GatewayContext gatewayContext = new GatewayContext(exchange);
        if (account == null) {
            if (isWhiteList(exchange)) {
                grayFilter.filter(gatewayContext);
                return chain.filter(exchange);
            }
            logger.warn("用户token 无效");
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            String json = loginMocker.buildUnAuthorize();
            DataBuffer buffer = response.bufferFactory().wrap(json.getBytes(StandardCharsets.UTF_8));
            return response.writeWith(Mono.just(buffer));
        } else {
            exchange.getRequest().mutate().header("user-id", account.getAccountId()).build();
            grayFilter.filter(gatewayContext);
            return chain.filter(exchange);
        }
    }

    private boolean isWhiteList(ServerWebExchange exchange) {
        //todo
        return true;
    }

    private Account getLoginAccount(ServerWebExchange exchange) {
        HttpHeaders headers = exchange.getRequest().getHeaders();
        String token = headers.getFirst("token");
        if (token == null) {
            logger.warn("用户token 无效");
            return null;
        }
        return loginMocker.getByToken(token);
    }

    @Override
    public int getOrder() {
        return 1010;
    }

}