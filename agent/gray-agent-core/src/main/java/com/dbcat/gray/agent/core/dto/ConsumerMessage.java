package com.dbcat.gray.agent.core.dto;

import com.dbcat.gray.agent.core.context.Context;

import static com.dbcat.gray.agent.core.conf.Config.Agent.ROUTE_LABEL;

public class ConsumerMessage<M> {

    private ComponentType type;

    private M msg;

    private String routingEnv;

    private Object[] allArguments;

    public static <M> ConsumerMessage build(ComponentType type, M msg, Context context, Object[] allArguments) {
        ConsumerMessage message = new ConsumerMessage();
        message.msg = msg;
        message.type = type;
        message.routingEnv = (String) context.get(ROUTE_LABEL);
        message.allArguments = allArguments;
        return message;
    }

    public String getRoutingEnv() {
        return routingEnv;
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

}
