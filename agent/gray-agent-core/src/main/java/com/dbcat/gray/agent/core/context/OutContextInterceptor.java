package com.dbcat.gray.agent.core.context;

import com.dbcat.gray.agent.core.util.ServerContextHolder;

import static com.dbcat.gray.agent.core.conf.Config.Agent.contextKeys;

/**
 * 出站接口
 *
 * @author Blackfost
 */
public interface OutContextInterceptor {


    default void setOutContext(Context context) {
        for (String key : contextKeys) {
            Object value = ServerContextHolder.get(key);
            if (value == null) {
                continue;
            }
            context.put(key, value);
        }

    }
}
