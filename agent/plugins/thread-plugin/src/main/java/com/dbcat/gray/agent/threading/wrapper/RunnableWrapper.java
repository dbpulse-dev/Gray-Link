package com.dbcat.gray.agent.threading.wrapper;

import com.dbcat.gray.agent.core.context.DefaultContext;
import com.dbcat.gray.agent.core.context.InContextInterceptor;

/**
 * @author Blackfost
 */
public class RunnableWrapper implements InContextInterceptor, Runnable {

    private Runnable runnable;

    private DefaultContext context;

    public RunnableWrapper(Runnable runnable) {
        this.runnable = runnable;
        context = DefaultContext.getDefaultContext();
    }

    @Override
    public void run() {
        this.setInContext(context);
        try {
            runnable.run();
        } finally {
            this.clear();
        }
    }
}
