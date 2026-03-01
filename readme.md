# Gray-Link 微服务全链路灰度发布系统

[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](LICENSE)
[![Java](https://img.shields.io/badge/java-8+-green.svg)](https://www.oracle.com/java/technologies/javase-downloads.html)
[![Spring Boot](https://img.shields.io/badge/spring--boot-2.2.4.RELEASE-brightgreen.svg)](https://spring.io/projects/spring-boot)

Gray-Link 是一个基于 Java Agent 的微服务全链路灰度发布解决方案，提供无侵入式的灰度发布能力，支持主流微服务框架和消息中间件。

## 🌟 特性

- **无侵入式设计**：基于 Java Agent 实现，无需修改业务代码
- **全链路支持**：覆盖微服务调用链、消息队列、Web 请求等多个场景
- **多框架兼容**：支持 Spring Cloud、Dubbo 等主流微服务框架
- **丰富中间件**：支持 Kafka、RocketMQ、RabbitMQ 等消息中间件
- **灵活路由**：支持基于用户、请求头、域名等多种灰度策略
- **实时管控**：提供管理后台进行实例状态和订阅策略的实时控制
- **流量统计**：完善的流量监控和统计数据

## 🏗️ 系统架构

Gray-Link 系统主要分为两个核心组件：

### 1. Agent 端（灰度代理）
- 基于 Java Agent 和 ByteBuddy 实现字节码增强
- 通过插件化架构支持多种中间件和框架
- 内置 Netty HTTP 服务与管理端通信
- 实现灰度标签在全链路的透传

### 2. Admin 端（管理后台）
- 基于 Spring Boot 的管理服务
- 提供 RESTful API 进行实例管理和策略配置
- 支持 Swagger UI 接口文档
- MySQL 数据库存储配置和统计信息

## 🔧 支持的组件

### 微服务框架
- ✅ Spring Cloud (含 Eureka、Nacos、Ribbon)
- ✅ Dubbo
- ✅ Spring Web MVC

### 消息中间件
- ✅ Kafka
- ✅ RocketMQ
- ✅ RabbitMQ

### 其他组件
- ✅ 多线程池标签透传
- ✅ 网关路由 (Zuul、Spring Cloud Gateway)

## 📦 项目结构

```
Gray-Link-gray-deploy2/
├── admin/                  # 管理后台
│   ├── src/main/java/
│   ├── src/main/resources/
│   └── doc/sql/           # 数据库脚本
├── agent/                 # 灰度代理
│   ├── gray-agent/        # Agent 主体
│   ├── gray-agent-core/   # Agent 核心模块
│   └── plugins/           # 插件模块
│       ├── cloud-plugin/  # Spring Cloud 插件
│       ├── dubbo-plugin/  # Dubbo 插件
│       ├── kafka-plugin/  # Kafka 插件
│       ├── rabbitmq-plugin/ # RabbitMQ 插件
│       ├── rocketmq-plugin/ # RocketMQ 插件
│       ├── web-plugin/    # Web 插件
│       └── thread-plugin/ # 线程插件
├── gray-filter/           # 灰度过滤器
├── spring-cloud-test/     # Spring Cloud 测试示例
├── spring-dubbo-test/     # Dubbo 测试示例
└── doc/                   # 文档资料
```

## 🚀 快速开始

### 环境要求

- JDK 8+
- Maven 3.6+
- MySQL 5.7+

### 编译构建

```bash
# 克隆项目
git clone https://github.com/your-org/Gray-Link-gray-deploy2.git
cd Gray-Link-gray-deploy2

# 编译整个项目
mvn clean package -DskipTests
```

构建完成后，主要产物位于：
- Agent JAR: `agent/gray-agent/target/gray-agent-1.0-SNAPSHOT.jar`
- Admin JAR: `admin/target/admin-1.0-SNAPSHOT.jar`

### 数据库初始化

```sql
# 执行数据库脚本
mysql -u username -p < admin/doc/sql/create_table.sql
```

### 启动管理后台

```bash
# 配置 application.yml
vim admin/src/main/resources/application-local.yml

# 启动管理服务
java -jar admin/target/admin-1.0-SNAPSHOT.jar
```

### 使用 Agent

在应用启动时添加 JVM 参数：

```bash
-javaagent:/path/to/gray-agent-1.0-SNAPSHOT.jar \
-Dgray.agent.admin_addr=http://localhost:8080 \
-Dgray.agent.service_name=your-service-name \
-Dgray.agent.env=local \
-Dgray.agent.env_status=1 \
-Dgray.agent.server_port=6666
```

## 🛠️ 配置说明

### Agent 配置参数

| 参数 | 说明 | 默认值 |
|------|------|--------|
| `gray.agent.admin_addr` | 管理端地址 | - |
| `gray.agent.service_name` | 服务名称 | - |
| `gray.agent.env` | 环境标识 | local |
| `gray.agent.env_status` | 环境状态 (0:灰度, 1:正常) | 1 |
| `gray.agent.server_port` | Agent HTTP 服务端口 | 6666 |

### 灰度策略配置

支持多种灰度策略：

1. **用户维度**：基于用户ID进行灰度
2. **请求头维度**：基于HTTP请求头进行灰度
3. **域名维度**：基于访问域名进行灰度
4. **权重分配**：按比例分配流量到灰度环境

## 📊 管理界面

系统提供 Swagger UI 接口文档：

```
http://localhost:8080/swagger-ui.html
```

主要管理功能：
- 实例管理：查看和控制服务实例状态
- 策略配置：配置灰度分流策略
- 流量监控：实时查看流量统计数据
- 订阅管理：控制消息队列订阅策略

## 🧪 测试示例

项目包含完整的测试示例：

### Spring Cloud 示例
```bash
cd spring-cloud-test
# 启动 Eureka 注册中心
java -jar cloud-eureka/target/cloud-eureka-1.0-SNAPSHOT.jar
# 启动服务提供者和消费者
```

### Dubbo 示例
```bash
cd spring-dubbo-test
# 启动 Nacos 注册中心
# 启动 Dubbo 服务提供者和消费者
```

## 📚 文档资源

- [灰度发布设计文档](doc/灰度发布.md)
- [CSDN](https://blog.csdn.net/yangyangiud/category_12865143.html)

## 🤝 贡献指南

欢迎提交 Issue 和 Pull Request！

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

## 📄 许可证

本项目采用 Apache License 2.0 许可证，详情请见 [LICENSE](LICENSE) 文件。

## 🙏 致谢

- 感谢 [SkyWalking](https://github.com/apache/skywalking) 提供的 Agent 核心实现参考
- 感谢所有贡献者的辛勤付出

---

**Gray-Link - 让灰度发布更简单！** 🚀
