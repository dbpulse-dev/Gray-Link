package com.dbcat.gray.filter.dto;


import java.util.List;


public class StrategyCondition {

    private String name;

    private List<ScopeConfig> scopes;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ScopeConfig> getScopes() {
        return scopes;
    }

    public void setScopes(List<ScopeConfig> scopes) {
        this.scopes = scopes;
    }
}
