package com.dbcat.gray.filter.parser;

import com.dbcat.gray.filter.Context;
import com.dbcat.gray.filter.dto.ScopeConfig;

public interface ScopeValueParser {

    boolean support(String scope);

    String parse(Context request, ScopeConfig config);
}
