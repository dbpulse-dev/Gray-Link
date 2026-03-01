package com.dbcat.gray.agent.agent;

import com.dbcat.gray.agent.agent.bytebuddy.SWAuxiliaryTypeNamingStrategy;
import com.dbcat.gray.agent.agent.bytebuddy.SWMethodGraphCompilerDelegate;
import com.dbcat.gray.agent.core.boot.AgentPackageNotFoundException;
import com.dbcat.gray.agent.core.boot.LoadedLibraryCollector;
import com.dbcat.gray.agent.core.boot.ServiceManager;
import com.dbcat.gray.agent.core.conf.Config;
import com.dbcat.gray.agent.core.conf.Constants;
import com.dbcat.gray.agent.core.conf.SnifferConfigInitializer;
import com.dbcat.gray.agent.core.logging.api.ILog;
import com.dbcat.gray.agent.core.logging.api.LogManager;
import com.dbcat.gray.agent.core.plugin.*;
import com.dbcat.gray.agent.core.plugin.bootstrap.BootstrapInstrumentBoost;
import com.dbcat.gray.agent.core.plugin.jdk9module.JDK9ModuleExporter;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.agent.builder.SWAgentBuilderDefault;
import net.bytebuddy.agent.builder.SWDescriptionStrategy;
import net.bytebuddy.agent.builder.SWNativeMethodStrategy;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.scaffold.MethodGraph;
import net.bytebuddy.dynamic.scaffold.TypeValidation;
import net.bytebuddy.implementation.SWImplementationContextFactory;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;

import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static net.bytebuddy.matcher.ElementMatchers.nameContains;
import static net.bytebuddy.matcher.ElementMatchers.nameStartsWith;

public class DbcatGrayAgent {
    private static ILog LOGGER = LogManager.getLogger(DbcatGrayAgent.class);


    public static void premain(String agentArgs, Instrumentation instrumentation) throws PluginException {
        final PluginFinder pluginFinder;
        try {
            SnifferConfigInitializer.initializeCoreConfig(agentArgs);
        } catch (Exception e) {
            // try to resolve a new logger, and use the new logger to write the error log here
            LogManager.getLogger(DbcatGrayAgent.class)
                    .error(e, "Gray agent initialized failure. Shutting down.");
            return;
        } finally {
            // refresh logger again after initialization finishes
            LOGGER = LogManager.getLogger(DbcatGrayAgent.class);
        }

        if (!Config.Agent.ENABLE) {
            LOGGER.warn("Gray agent is disabled.");
            return;
        }

        try {
            pluginFinder = new PluginFinder(new PluginBootstrap().loadPlugins());
        } catch (AgentPackageNotFoundException ape) {
            LOGGER.error(ape, "Locate agent.jar failure. Shutting down.");
            return;
        } catch (Exception e) {
            LOGGER.error(e, "Gray agent initialized failure. Shutting down.");
            return;
        }

        try {
            installClassTransformer(instrumentation, pluginFinder);
        } catch (Exception e) {
            LOGGER.error(e, "Gray agent installed class transformer failure.");
        }

        LOGGER.info("启动 agent service .. ");
        try {
            ServiceManager.INSTANCE.boot();
        } catch (Exception e) {
            LOGGER.error(e, "Gray agent boot failure.");
        }

        Runtime.getRuntime()
                .addShutdownHook(new Thread(ServiceManager.INSTANCE::shutdown, "gray service shutdown thread"));
    }

    static void installClassTransformer(Instrumentation instrumentation, PluginFinder pluginFinder) throws Exception {
        LOGGER.info("Gray agent begin to install transformer ...");

        AgentBuilder agentBuilder = newAgentBuilder().ignore(
                nameStartsWith("net.bytebuddy.")
                        .or(nameStartsWith("com.dbcat.gray.agent."))
                        .or(nameStartsWith("org.slf4j."))
                        .or(nameStartsWith("org.groovy."))
                        .or(nameContains("javassist"))
                        .or(nameContains(".asm."))
                        .or(nameContains(".reflectasm."))
                        .or(nameStartsWith("sun."))
                        .or(nameStartsWith("com.sum"))
                        .or(nameStartsWith("com.oracle"))
                        .or(nameStartsWith("javax."))
                        .or(nameStartsWith("java.lang"))
                        .or(nameStartsWith("jdk."))
                        .or(ElementMatchers.isSynthetic()));


        JDK9ModuleExporter.EdgeClasses edgeClasses = new JDK9ModuleExporter.EdgeClasses();
        try {
            agentBuilder = BootstrapInstrumentBoost.inject(pluginFinder, instrumentation, agentBuilder, edgeClasses);
        } catch (Exception e) {
            throw new Exception("Gray agent inject bootstrap instrumentation failure. Shutting down.", e);
        }

        try {
            agentBuilder = JDK9ModuleExporter.openReadEdge(instrumentation, agentBuilder, edgeClasses);
        } catch (Exception e) {
            throw new Exception("Gray agent open read edge in JDK 9+ failure. Shutting down.", e);
        }

        agentBuilder.type(pluginFinder.buildMatch())
                .transform(new Transformer(pluginFinder))
                .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
                .with(new RedefinitionListener())
                .with(new Listener())
                .installOn(instrumentation);

        PluginFinder.pluginInitCompleted();

        LOGGER.info("Gray agent transformer has installed.");
    }


    private static AgentBuilder newAgentBuilder() {
        final ByteBuddy byteBuddy = new ByteBuddy()
                .with(TypeValidation.of(Config.Agent.IS_OPEN_DEBUGGING_CLASS))
                .with(new SWAuxiliaryTypeNamingStrategy(Constants.NAME_TRAIT))
                .with(new SWImplementationContextFactory(Constants.NAME_TRAIT))
                .with(new SWMethodGraphCompilerDelegate(MethodGraph.Compiler.DEFAULT));

        return new SWAgentBuilderDefault(byteBuddy, new SWNativeMethodStrategy(Constants.NAME_TRAIT))
                .with(new SWDescriptionStrategy(Constants.NAME_TRAIT));
    }

    private static class Transformer implements AgentBuilder.Transformer {
        private PluginFinder pluginFinder;

        Transformer(PluginFinder pluginFinder) {
            this.pluginFinder = pluginFinder;
        }

        @Override
        public DynamicType.Builder<?> transform(final DynamicType.Builder<?> builder,
                                                final TypeDescription typeDescription,
                                                final ClassLoader classLoader,
                                                final JavaModule javaModule,
                                                final ProtectionDomain protectionDomain) {
            LoadedLibraryCollector.registerURLClassLoader(classLoader);
            List<AbstractClassEnhancePluginDefine> pluginDefines = pluginFinder.find(typeDescription);
            if (pluginDefines.size() > 0) {
                DynamicType.Builder<?> newBuilder = builder;
                EnhanceContext context = new EnhanceContext();
                for (AbstractClassEnhancePluginDefine define : pluginDefines) {
                    DynamicType.Builder<?> possibleNewBuilder = define.define(
                            typeDescription, newBuilder, classLoader, context);
                    if (possibleNewBuilder != null) {
                        newBuilder = possibleNewBuilder;
                    }
                }
                if (context.isEnhanced()) {
                    LOGGER.debug("Finish the prepare stage for {}.", typeDescription.getName());
                }

                return newBuilder;
            }

            LOGGER.debug("Matched class {}, but ignore by finding mechanism.", typeDescription.getTypeName());
            return builder;
        }
    }


    private static class Listener implements AgentBuilder.Listener {
        @Override
        public void onDiscovery(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded) {

        }

        @Override
        public void onTransformation(final TypeDescription typeDescription,
                                     final ClassLoader classLoader,
                                     final JavaModule module,
                                     final boolean loaded,
                                     final DynamicType dynamicType) {
            if (LOGGER.isDebugEnable()) {
                LOGGER.debug("On Transformation class {}.", typeDescription.getName());
            }

            InstrumentDebuggingClass.INSTANCE.log(dynamicType);
        }

        @Override
        public void onIgnored(final TypeDescription typeDescription,
                              final ClassLoader classLoader,
                              final JavaModule module,
                              final boolean loaded) {

        }

        @Override
        public void onError(final String typeName,
                            final ClassLoader classLoader,
                            final JavaModule module,
                            final boolean loaded,
                            final Throwable throwable) {
            LOGGER.error("Enhance class " + typeName + " error.", throwable);
        }

        @Override
        public void onComplete(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded) {
        }
    }

    private static class RedefinitionListener implements AgentBuilder.RedefinitionStrategy.Listener {

        @Override
        public void onBatch(int index, List<Class<?>> batch, List<Class<?>> types) {
            /* do nothing */
        }

        @Override
        public Iterable<? extends List<Class<?>>> onError(int index,
                                                          List<Class<?>> batch,
                                                          Throwable throwable,
                                                          List<Class<?>> types) {
            LOGGER.error(throwable, "index={}, batch={}, types={}", index, batch, types);
            return Collections.emptyList();
        }

        @Override
        public void onComplete(int amount, List<Class<?>> types, Map<List<Class<?>>, Throwable> failures) {
            /* do nothing */
        }
    }
}
