package com.dbcat.gray.agent.core.plugin;

import com.dbcat.gray.agent.core.logging.api.ILog;
import com.dbcat.gray.agent.core.logging.api.LogManager;
import com.dbcat.gray.agent.core.plugin.loader.AgentClassLoader;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;


public class PluginResourcesResolver {
    private static final ILog LOGGER = LogManager.getLogger(PluginResourcesResolver.class);

    public List<URL> getResources() {
        List<URL> cfgUrlPaths = new ArrayList<>();
        Enumeration<URL> urls;
        try {
            urls = AgentClassLoader.getDefault().getResources("dbcat-gray-plugin.def");

            while (urls.hasMoreElements()) {
                URL pluginUrl = urls.nextElement();
                cfgUrlPaths.add(pluginUrl);
                LOGGER.info("find gray plugin define in {}", pluginUrl);
            }

            return cfgUrlPaths;
        } catch (IOException e) {
            LOGGER.error("read resources failure.", e);
        }
        return null;
    }
}
