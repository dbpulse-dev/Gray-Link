package com.dbcat.gray.agent.core.context;

import com.dbcat.gray.agent.core.util.ServerContextHolder;

import static com.dbcat.gray.agent.core.conf.Config.Agent.contextKeys;

/**
 * 入站接口
 *
 * @author Blackfost
 */
public interface InContextInterceptor {


    default void setInContext(Context context) {
        for (String key : contextKeys) {
            Object value = context.get(key);
            if (value == null) {
                continue;
            }
            ServerContextHolder.set(key, value);
        }

    }


    default void clear() {
        ServerContextHolder.clear();
    }


    default void remove(String key) {
        ServerContextHolder.remove(key);
    }

}
