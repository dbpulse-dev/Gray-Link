package com.dbcat.gray.agent.core.context;

import com.dbcat.gray.agent.core.util.ServerContextHolder;

import java.util.HashMap;
import java.util.Map;

import static com.dbcat.gray.agent.core.conf.Config.Agent.contextKeys;

/**
 * @author Blackfost
 */
public class DefaultContext implements Context {

    private Map<String, Object> ctx;


    public DefaultContext(Map<String, Object> ctx) {
        this.ctx = ctx;
    }

    @Override
    public void put(String key, Object value) {
        ctx.put(key, value);
    }

    @Override
    public Object get(String key) {
        return ctx.get(key);
    }


    public static DefaultContext getDefaultContext() {
        DefaultContext defaultContext = new DefaultContext(new HashMap<>());
        if (contextKeys == null) {
            return defaultContext;
        }
        for (String key : contextKeys) {
            Object value = ServerContextHolder.get(key);
            if (value == null) {
                continue;
            }
            defaultContext.put(key, value);
        }
        return defaultContext;
    }
}
