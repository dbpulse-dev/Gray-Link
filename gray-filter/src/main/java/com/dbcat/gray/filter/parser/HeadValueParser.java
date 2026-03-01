package com.dbcat.gray.filter.parser;

import com.dbcat.gray.filter.Context;
import com.dbcat.gray.filter.dto.ScopeConfig;

public class HeadValueParser implements ScopeValueParser {


    @Override
    public boolean support(String scope) {
        return "header".equals(scope);
    }

    @Override
    public String parse(Context request, ScopeConfig config) {
        return request.getRequestHeader(config.getName());
    }
}

