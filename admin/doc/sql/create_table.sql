-- gray_admin.app_instance definition

CREATE TABLE gray_admin.`app_instance` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'id',
  `app_name` varchar(64) DEFAULT NULL COMMENT '应用名称',
  `env` varchar(10) DEFAULT NULL COMMENT '环境；dev、sit、uat、prod',
  `ip` varchar(50) DEFAULT NULL COMMENT 'ip地址',
  `hostname` varchar(50) DEFAULT NULL COMMENT '主机名',
  `agent_port` int DEFAULT NULL COMMENT 'agent服务端口',
  `status` tinyint DEFAULT NULL COMMENT '状态；1：运行中，2：停止，3：异常',
  `env_status` tinyint DEFAULT NULL COMMENT '实例环境状态，1正常状态,0 灰度状态，默认为1',
  `subscript_status` tinyint DEFAULT NULL COMMENT '0:仅订阅灰度消息，1仅订阅正常消息，2同时订阅正常和灰度消息',
  `report_at` datetime DEFAULT NULL COMMENT '最后上报时间，可以作为最后重启时间',
  `check_at` datetime DEFAULT NULL COMMENT '最后检测时间',
  `deleted` tinyint DEFAULT '0' COMMENT '是否删除；0：否、 1：是',
  `create_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `version` varchar(32) DEFAULT NULL COMMENT '应用版本',
  `agent_version` varchar(32) DEFAULT NULL COMMENT 'agent版本',
  `uuid` varchar(36) DEFAULT NULL COMMENT '实例uuid',
  `start_at` datetime DEFAULT NULL COMMENT '启动时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uuid_IDX` (`uuid`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='应用实例';


-- gray_admin.instance_traffic definition

CREATE TABLE gray_admin.`instance_traffic` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'id',
  `app_name` varchar(64) DEFAULT NULL COMMENT '应用名称',
  `uuid` varchar(36) DEFAULT NULL COMMENT '实例uuid',
  `direction` varchar(8) DEFAULT NULL COMMENT 'produce,consume',
  `name` varchar(36) DEFAULT NULL COMMENT '统计名称',
  `type` tinyint DEFAULT '0' COMMENT '0：灰度、 1：正常',
  `value` bigint DEFAULT '0' COMMENT '访问量',
  `update_at` datetime default null COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uuid_IDX` (`uuid`,`type`,`name`,`direction`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='实例流量';

CREATE TABLE gray_admin.`app_version` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'id',
  `app_name` varchar(64) DEFAULT NULL COMMENT '应用名称',
  `env` varchar(10) DEFAULT NULL COMMENT '环境；dev、sit、uat、prod',
  `env_status` tinyint DEFAULT NULL COMMENT '实例环境状态，1正常状态,0 灰度状态，默认为1',
  `version` varchar(32) DEFAULT NULL COMMENT '应用版本',
  `instance_count` tinyint DEFAULT '0' COMMENT '实例数',
  `deleted` tinyint DEFAULT '0' COMMENT '是否删除；0：否、 1：是',
  `update_at` datetime default null COMMENT '更新时间',
  `create_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='应用版本配置信息';


CREATE TABLE gray_admin.`gray_condition` (
  `id` int NOT NULL AUTO_INCREMENT,
  `type` varchar(50) DEFAULT NULL COMMENT '类型:user，client,domain,header',
  `value` varchar(256) DEFAULT NULL COMMENT '灰度条件值',
  `access_total_count` bigint DEFAULT '0' COMMENT '总的访问量统计',
  `access_gray_count` bigint DEFAULT '0' COMMENT '灰度的访问量统计',
  `weight` int NOT NULL DEFAULT '100' COMMENT '流量权重,范围1-100',
  `operators` varchar(50) DEFAULT 'EQ' COMMENT '操作类型:EQ，GT,LT多选用#分割',
  `enable` tinyint DEFAULT '1' COMMENT '是否启用；0：否、 1：是',
  `deleted` tinyint DEFAULT '0' COMMENT '是否删除；0：否、 1：是',
  `create_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='灰度条件配置';
