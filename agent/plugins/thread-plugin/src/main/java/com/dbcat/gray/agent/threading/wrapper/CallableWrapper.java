package com.dbcat.gray.agent.threading.wrapper;


import com.dbcat.gray.agent.core.context.DefaultContext;
import com.dbcat.gray.agent.core.context.InContextInterceptor;

import java.util.concurrent.Callable;

/**
 * @author Blackfost
 */
public class CallableWrapper implements InContextInterceptor, Callable {

    private Callable callable;

    private DefaultContext context;

    public CallableWrapper(Callable callable) {
        this.callable = callable;
        context = DefaultContext.getDefaultContext();
    }

    @Override
    public Object call() throws Exception {
        this.setInContext(context);
        try {
            return callable.call();
        } finally {
            this.clear();
        }
    }

}
