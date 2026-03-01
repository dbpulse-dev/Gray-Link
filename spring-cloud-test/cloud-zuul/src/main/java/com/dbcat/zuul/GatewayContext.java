package com.dbcat.zuul;

import com.dbcat.gray.filter.Context;
import com.netflix.zuul.context.RequestContext;

import javax.servlet.http.Cookie;
import java.util.HashMap;
import java.util.Map;


public class GatewayContext implements Context {
    private Map<String, Object> attr = new HashMap<>();
    private RequestContext ctx;

    public GatewayContext(RequestContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public String getRequestHeader(String key) {
        return ctx.getRequest().getHeader(key);
    }

    @Override
    public void setRequestHeader(String key, String value) {
        ctx.addZuulRequestHeader(key, value);
    }

    @Override
    public String getCookie(String name) {
        Cookie[] cookies = ctx.getRequest().getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(name)) {
                return cookie.getValue();
            }
        }
        return null;
    }

    @Override
    public void setResponseHeader(String name, String value) {
        ctx.addZuulResponseHeader(name, value);
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
