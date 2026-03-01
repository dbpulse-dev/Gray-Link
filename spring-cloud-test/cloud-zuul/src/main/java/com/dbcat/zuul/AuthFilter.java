package com.dbcat.zuul;

import com.dbcat.gateway.gray.Account;
import com.dbcat.gateway.gray.LoginMocker;
import com.dbcat.gray.filter.GrayFilter;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;


@Component
public class AuthFilter extends ZuulFilter {

    protected static final Logger logger = LoggerFactory.getLogger(AuthFilter.class);
    @Autowired
    private LoginMocker loginMocker;


    @Autowired
    private GrayFilter grayFilter;

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext ctx = RequestContext.getCurrentContext();
        GatewayContext gatewayContext = new GatewayContext(ctx);
        HttpServletRequest request = ctx.getRequest();
        Account account = getLoginAccount(request);
        if (account == null) {
            if (isWhiteList(ctx)) {
                grayFilter.filter(gatewayContext);
            } else {
                ctx.setSendZuulResponse(false);
                ctx.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
                String body = loginMocker.buildUnAuthorize();
                ctx.setResponseBody(body);
            }
        } else {
            ctx.addZuulRequestHeader("user-id", account.getAccountId());
            grayFilter.filter(gatewayContext);
        }
        return null;

    }

    private Account getLoginAccount(HttpServletRequest request) {
        String token = request.getHeader("token");
        if (token == null) {
            logger.warn("用户token 无效");
            return null;
        }
        return loginMocker.getByToken(token);
    }

    private boolean isWhiteList(RequestContext ctx) {
        //todo
        return true;
    }

}