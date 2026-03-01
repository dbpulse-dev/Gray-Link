package com.dbcat.gray.filter.parser;

import com.dbcat.gray.filter.Context;
import com.dbcat.gray.filter.dto.ScopeConfig;

import java.util.List;


public class CompositeScopeValueParser implements ScopeValueParser {

    private List<ScopeValueParser> valueParsers;


    public CompositeScopeValueParser(List<ScopeValueParser> valueParsers) {
        this.valueParsers = valueParsers;
    }

    @Override
    public boolean support(String scope) {
        return false;
    }

    @Override
    public String parse(Context request, ScopeConfig config) {
        for (ScopeValueParser parser : valueParsers) {
            if (parser.support(config.getScope())) {
                return parser.parse(request, config);
            }
        }
        return null;
    }
}
