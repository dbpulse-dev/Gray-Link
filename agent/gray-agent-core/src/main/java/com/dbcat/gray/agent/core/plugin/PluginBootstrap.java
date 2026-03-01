package com.dbcat.gray.agent.core.plugin;

import com.dbcat.gray.agent.core.boot.AgentPackageNotFoundException;
import com.dbcat.gray.agent.core.logging.api.ILog;
import com.dbcat.gray.agent.core.logging.api.LogManager;
import com.dbcat.gray.agent.core.plugin.loader.AgentClassLoader;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class PluginBootstrap {
    private static final ILog LOGGER = LogManager.getLogger(PluginBootstrap.class);


    public List<AbstractClassEnhancePluginDefine> loadPlugins() throws AgentPackageNotFoundException {
        AgentClassLoader.initDefaultLoader();

        PluginResourcesResolver resolver = new PluginResourcesResolver();
        List<URL> resources = resolver.getResources();

        if (resources == null || resources.size() == 0) {
            LOGGER.info("no plugin files found, continue to start application.");
            return new ArrayList<AbstractClassEnhancePluginDefine>();
        }

        for (URL pluginUrl : resources) {
            try {
                PluginCfg.INSTANCE.load(pluginUrl.openStream());
            } catch (Throwable t) {
                LOGGER.error(t, "plugin file [{}] init failure.", pluginUrl);
            }
        }

        List<PluginDefine> pluginClassList = PluginCfg.INSTANCE.getPluginClassList();

        List<AbstractClassEnhancePluginDefine> plugins = new ArrayList<AbstractClassEnhancePluginDefine>();
        for (PluginDefine pluginDefine : pluginClassList) {
            try {
                LOGGER.debug("loading plugin class {}.", pluginDefine.getDefineClass());
                AbstractClassEnhancePluginDefine plugin = (AbstractClassEnhancePluginDefine) Class.forName(pluginDefine.getDefineClass(), true, AgentClassLoader
                        .getDefault()).newInstance();
                plugins.add(plugin);
            } catch (Throwable t) {
                LOGGER.error(t, "load plugin [{}] failure.", pluginDefine.getDefineClass());
            }
        }

        plugins.addAll(DynamicPluginLoader.INSTANCE.load(AgentClassLoader.getDefault()));
        return plugins;

    }

}
