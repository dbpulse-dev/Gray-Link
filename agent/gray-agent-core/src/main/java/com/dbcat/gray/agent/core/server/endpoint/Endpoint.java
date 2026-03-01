package com.dbcat.gray.agent.core.server.endpoint;


import com.dbcat.gray.agent.core.server.RestResult;

/**
 * @param <RQ>
 * @param <RS>
 * @author Blackfost
 */
public interface Endpoint<RQ, RS> {

    String path();

    RestResult<RS> invoke(RQ data) throws Exception;
}
