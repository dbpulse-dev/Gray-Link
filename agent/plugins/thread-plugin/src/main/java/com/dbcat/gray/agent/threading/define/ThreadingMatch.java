package com.dbcat.gray.agent.threading.define;


import com.dbcat.gray.agent.core.conf.Config;
import com.dbcat.gray.agent.core.logging.api.ILog;
import com.dbcat.gray.agent.core.logging.api.LogManager;
import com.dbcat.gray.agent.core.plugin.match.HierarchyMatch;
import net.bytebuddy.description.type.TypeDescription;

import java.util.List;

/**
 * @author Blackfost
 */
public class ThreadingMatch extends HierarchyMatch {
    private static final ILog log = LogManager.getLogger(ThreadingMatch.class);

    public ThreadingMatch(String... parentTypes) {
        super(parentTypes);
    }

    @Override
    public boolean isMatch(TypeDescription typeDescription) {
        if (!(super.isMatch(typeDescription))) {
            return false;
        }
        if (typeDescription.getName().startsWith("java.")) {
            return false;
        }
        if (typeDescription.getName().startsWith("javax.")) {
            return false;
        }
        List<String> runnablePackage = Config.Agent.ENHANCE_THREAD_TASK_PACKAGES;
        if (runnablePackage == null || runnablePackage.isEmpty()) {
            return false;
        }
        for (String pk : runnablePackage) {
            if (typeDescription.getName().startsWith(pk)) {
                log.info("enhance class {}", typeDescription.getName());
                return true;
            }
        }
        return false;
    }
}
