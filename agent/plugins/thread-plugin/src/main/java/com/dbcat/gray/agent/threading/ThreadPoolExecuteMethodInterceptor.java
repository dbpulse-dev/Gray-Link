package com.dbcat.gray.agent.threading;


import com.dbcat.gray.agent.threading.wrapper.RunnableWrapper;

import java.util.concurrent.RunnableFuture;

/**
 * @author Blackfost
 */
public class ThreadPoolExecuteMethodInterceptor extends AbstractThreadPoolInterceptor {

    @Override
    public Object wrap(Object param) {
        if (param instanceof RunnableWrapper) {
            return null;
        }

        if (param instanceof RunnableFuture) {
            return null;
        }

        if (!(param instanceof Runnable)) {
            return null;
        }

        Runnable runnable = (Runnable) param;
        return new RunnableWrapper(runnable);
    }
}
