package com.dbcat.gray.agent.core.dto;

import com.dbcat.gray.agent.core.context.Context;

import static com.dbcat.gray.agent.core.conf.Config.Agent.ROUTE_LABEL;

public class PublishMessage<M> {
    private ComponentType type;

    private String routingEnv;

    private M msg;

    private Object[] allArguments;

    public String getRoutingEnv() {
        return routingEnv;
    }

    public static <M> PublishMessage build(ComponentType type, M msg, Context context, Object[] allArguments) {
        PublishMessage message = new PublishMessage();
        message.msg = msg;
        message.type = type;
        message.allArguments = allArguments;
        message.routingEnv = (String) context.get(ROUTE_LABEL);
        return message;
    }


    public M getMsg() {
        return msg;
    }

    public ComponentType getType() {
        return type;
    }

    public void setMsg(M msg) {
        this.msg = msg;
    }

    public Object[] getAllArguments() {
        return allArguments;
    }

    public void setAllArguments(Object[] allArguments) {
        this.allArguments = allArguments;
    }
}
