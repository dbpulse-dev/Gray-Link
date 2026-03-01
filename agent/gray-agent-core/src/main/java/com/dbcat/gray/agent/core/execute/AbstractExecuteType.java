package com.dbcat.gray.agent.core.execute;

public abstract class AbstractExecuteType implements ExecuteType {
    protected String name;

    public void setName(String name) {
        this.name = name;
    }
}
