package com.dbcat.gray.filter.parser;

import com.dbcat.gray.filter.Context;
import com.dbcat.gray.filter.dto.ScopeConfig;

public class CookieValueParser implements ScopeValueParser {


    @Override
    public boolean support(String scope) {
        return "cookie".equals(scope);
    }

    @Override
    public String parse(Context request, ScopeConfig config) {
        return request.getCookie(config.getName());
    }
}

