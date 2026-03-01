/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.dbcat.gray.agent.core.conf;

import com.dbcat.gray.agent.core.logging.core.*;
import com.dbcat.gray.agent.util.Length;

import java.util.Arrays;
import java.util.List;

/**
 * This is the core config in sniffer agent.
 */
public class Config {

    public static class Agent {

        public static int SERVER_PORT = 1688;
        /**
         * Namespace represents a subnet, such as kubernetes namespace, or 172.10.*.*.
         *
         * @since 8.10.0 namespace would be added as {@link #SERVICE_NAME} suffix.
         * <p>
         * Removed namespace isolating headers in cross process propagation. The HEADER name was
         * `HeaderName:Namespace`.
         */
        @Length(20)
        public static String NAMESPACE = "";

        /**
         * Service name is showed on the UI. Suggestion: set a unique name for each service, service instance nodes
         * share the same code
         *
         * @since 8.10.0 ${service name} = [${group name}::]${logic name}|${NAMESPACE}|${CLUSTER}
         * <p>
         * The group name, namespace and cluster are optional. Once they are all blank, service name would be the final
         * name.
         */
        @Length(50)
        public static String SERVICE_NAME = "";


        @Length(10)
        public static String ENV = "";

        @Length(10)
        public static String ROUTE_LABEL = "x_env";

        public static List<String> contextKeys = Arrays.asList("x_env");

        public static int ENV_STATUS = 1;

        /**
         * 0:仅订阅灰度消息，1仅订阅正常消息，2同时订阅正常和灰度消息
         */
        public static int SUBSCRIPT_STATUS = -1;

        /**
         * 消息过滤模式,0客户端过滤，1服务端过滤
         */
        public static int MSG_FILTER_MODE = 1;

        public static String ADMIN_ADDR;

        public static String REPORT_URL = "/app/instance/report";

        public static String APP_INSTANCES_SUBSCRIPT_STATUS_URL = "/gray/instance/subscript/status";

        public static String APP_DEPLOY_ENV_STATUS_URL = "/gray/app/deploy/env-status";

        public static String APP_INSTANCES_PULL_URL = "/app/instance/pull";

        public static String TRAFFIC_REPORT_URL = "/app/instance/traffic/report";


        /**
         * Cluster defines the physical cluster in a data center or same network segment. In one cluster, IP address
         * should be unique identify.
         * <p>
         * The cluster name would be
         * <p>
         * 1. Add as {@link #SERVICE_NAME} suffix.
         * <p>
         * 2. Add as exit span's peer, ${CLUSTER} / original peer
         * <p>
         * 3. Cross Process Propagation Header's value addressUsedAtClient[index=8] (Target address of this request used
         * on the client end).
         *
         * @since 8.10.0
         */
        @Length(20)
        public static String CLUSTER = "";



        /**
         * If true, SkyWalking agent will save all instrumented classes files in `/debugging` folder. SkyWalking team
         * may ask for these files in order to resolve compatible problem.
         */
        public static boolean IS_OPEN_DEBUGGING_CLASS = false;

        /**
         * The identifier of the instance
         */
        @Length(50)
        public volatile static String INSTANCE_NAME = "";

        /**
         * Agent version. This is set by the agent kernel through reading MANIFEST.MF file in the skywalking-agent.jar.
         */
        public static String APP_VERSION = "UNKNOWN";

        public static String VERSION = "1.0";

        /**
         * Enable the agent kernel services and instrumentation.
         */
        public static boolean ENABLE = true;

        public static List<String> ENHANCE_THREAD_TASK_PACKAGES;

        public static List<String> ENHANCE_THREAD_POOL_PACKAGES;
    }


    public static class Log {
        /**
         * The max size of message to send to server.Default is 10 MB.
         */
        public static int MAX_MESSAGE_SIZE = 10 * 1024 * 1024;
    }

    public static class Logging {
        /**
         * Log file name.
         */
        public static String FILE_NAME = "skywalking-api.log";

        /**
         * Log files directory. Default is blank string, means, use "{theSkywalkingAgentJarDir}/logs  " to output logs.
         * {theSkywalkingAgentJarDir} is the directory where the skywalking agent jar file is located.
         * <p>
         * Ref to {@link WriterFactory#getLogWriter()}
         */
        public static String DIR = "";

        /**
         * The max size of log file. If the size is bigger than this, archive the current file, and write into a new
         * file.
         */
        public static int MAX_FILE_SIZE = 300 * 1024 * 1024;

        /**
         * The max history log files. When rollover happened, if log files exceed this number, then the oldest file will
         * be delete. Negative or zero means off, by default.
         */
        public static int MAX_HISTORY_FILES = -1;

        /**
         * The log level. Default is debug.
         */
        public static LogLevel LEVEL = LogLevel.DEBUG;

        /**
         * The log output. Default is FILE.
         */
        public static LogOutput OUTPUT = LogOutput.FILE;

        /**
         * The log resolver type. Default is PATTERN which will create PatternLogResolver later.
         */
        public static ResolverType RESOLVER = ResolverType.PATTERN;

        /**
         * The log patten. Default is "%level %timestamp %thread %class : %msg %throwable". Each conversion specifiers
         * starts with a percent sign '%' and fis followed by conversion word. There are some default conversion
         * specifiers: %thread = ThreadName %level = LogLevel  {@link LogLevel} %timestamp = The now() who format is
         * 'yyyy-MM-dd HH:mm:ss:SSS' %class = SimpleName of TargetClass %msg = Message of user input %throwable =
         * Throwable of user input %agent_name = ServiceName of Agent {@link Agent#SERVICE_NAME}
         *
         * @see PatternLogger#DEFAULT_CONVERTER_MAP
         */
        public static String PATTERN = "%level %timestamp %thread %class : %msg %throwable";
    }


    public static class Plugin {
        /**
         * Control the length of the peer field.
         */
        public static int PEER_MAX_LENGTH = 200;

        /**
         * Exclude activated plugins
         */
        public static String EXCLUDE_PLUGINS = "";

        /**
         * Mount the folders of the plugins. The folder path is relative to agent.jar.
         */
        public static List<String> MOUNT = Arrays.asList("plugins", "activations");
    }

}
