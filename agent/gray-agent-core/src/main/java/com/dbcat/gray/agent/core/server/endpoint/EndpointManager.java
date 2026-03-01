package com.dbcat.gray.agent.core.server.endpoint;


import com.dbcat.gray.agent.core.plugin.loader.AgentClassLoader;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;


/**
 * @author Blackfost
 */
public class EndpointManager {
    private static final Map<String, Endpoint> ENDPOINT_INVOKER_MAP = new HashMap<>();

    static {
        for (final Endpoint endpointInvoker : ServiceLoader.load(Endpoint.class, AgentClassLoader.getDefault())) {
            ENDPOINT_INVOKER_MAP.put(endpointInvoker.path(), endpointInvoker);
        }
    }

    public static Endpoint<?, ?> getEndpoint(String requestUri) {
        return ENDPOINT_INVOKER_MAP.get(requestUri);
    }
}
