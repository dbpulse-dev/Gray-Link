package com.dbcat.gray.agent.core.context;


/**
 * context  接口用于匹配各种中间件
 * <p>
 *
 * @author Blackfost
 * @version 1.0.0
 * @date 2020/3/4
 */
public interface Context extends Cloneable {

    void put(String key, Object value);

    Object get(String key);

    default void remove(String key) {

    }

    default boolean in() {
        return true;
    }

    default void setIn(boolean in) {

    }

}
