package com.dbcat.gray.agent.core.server;

import java.util.List;

/**
 * 服务选择接口
 *
 * @param <S>
 * @author Blackfost
 */
public interface ServerSelector<S> {

    List<S> selectServers();

}
