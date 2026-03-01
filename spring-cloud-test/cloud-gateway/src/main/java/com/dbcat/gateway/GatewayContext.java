package com.dbcat.gateway;

import com.dbcat.gray.filter.Context;
import org.springframework.http.HttpCookie;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;

import java.util.HashMap;
import java.util.Map;

import static com.dbcat.gray.filter.RouteLabelUtils.ROUTE_LABEL;


public class GatewayContext implements Context {
    private Map<String, Object> attr = new HashMap<>();
    private ServerWebExchange exchange;

    public GatewayContext(ServerWebExchange exchange) {
        this.exchange = exchange;
    }

    @Override
    public String getRequestHeader(String key) {
        return exchange.getRequest().getHeaders().getFirst(key);
    }

    @Override
    public void setRequestHeader(String key, String value) {
        exchange.getRequest().mutate().header(ROUTE_LABEL, "1").build();
    }

    @Override
    public String getCookie(String name) {
        MultiValueMap<String, HttpCookie> cookies = exchange.getRequest().getCookies();
        HttpCookie first = cookies.getFirst(name);
        if (first == null) {
            return null;
        }
        return first.getValue();
    }

    @Override
    public void setResponseHeader(String name, String value) {
        exchange.getResponse().getHeaders().set(name, value);
    }

    @Override
    public Object getAttr(String key) {
        return attr.get(key);
    }

    @Override
    public void setAttr(String key, Object value) {
        attr.put(key, value);
    }
}
