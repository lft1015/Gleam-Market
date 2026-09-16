# 拾光集市：二手交易与失物招领平台

一个面向校园和社区场景的 **Java 后端项目**，用于解决闲置物品流转、失物招领和信息管理分散的问题。

项目重点不在前端页面，而在后端工程能力：用户认证、权限控制、内容审核、状态流转、缓存、异步消息、接口测试和容器化部署。

## 项目定位

“拾光集市”提供二手商品和失物招领两类核心业务。用户可以发布、搜索、收藏和沟通，管理员可以审核内容、处理举报和管理违规账号。

本项目按照后端求职项目设计，采用模块化单体架构，优先保证业务闭环和代码质量，不在第一版盲目拆分微服务。

## 核心功能

### 用户与权限

- 用户注册、登录、退出和个人资料维护
- JWT 登录认证
- 普通用户、认证用户、管理员和超级管理员权限区分
- 用户状态管理和违规账号处理

### 二手交易

- 发布、编辑、审核和下架二手商品
- 商品分类、关键词搜索、价格/成色/区域筛选
- 商品状态流转：待审核、在售、交易中、已售出、已下架
- 收藏、留言和站内会话

### 失物招领

- 发布寻物或拾物信息
- 按关键词、地点、时间和类型筛选
- 认领申请和私密验证信息
- 状态流转：进行中、处理中、已找回、已归还、已关闭

### 平台管理

- 内容审核、拒绝和下架
- 举报提交、风险分级和处理记录
- 用户警告、限制发言、临时封禁和永久封禁
- 分类、公告和操作日志管理

## 技术栈

### 后端核心

- Java 17
- Spring Boot 3
- Spring MVC
- Spring Security + JWT
- MyBatis-Plus
- Maven

### 数据与中间件

- MySQL 8：核心业务数据
- Redis：缓存、登录状态、限流和验证码
- RabbitMQ：异步通知、任务处理和数据导出
- MinIO 或对象存储：图片和附件元数据

### 工程化与测试

- Docker / Docker Compose
- Nginx
- Linux
- Git / GitHub
- Swagger / OpenAPI
- JUnit 5、Mockito、Spring Boot Test
- Apifox / Postman
- JMeter 或 Apache Bench
- SLF4J / Logback

## 系统结构

```text
客户端 / Apifox / Swagger UI
              |
         Nginx（可选）
              |
      Spring Boot REST API
       |       |        |
     MySQL   Redis   RabbitMQ
                         |
                 异步通知 / 导出任务
              |
       MinIO / 对象存储（可选）
```

项目第一阶段采用模块化单体架构，建议按以下边界组织代码：

```text
com.shiguang.market
├── common          # 统一响应、异常、工具类、枚举
├── config          # Spring、Security、Redis、MQ 配置
├── auth            # 注册、登录、Token 和权限
├── user            # 用户资料和账号状态
├── item            # 二手商品
├── lostfound       # 失物招领
├── message         # 会话和消息
├── favorite        # 收藏
├── report          # 举报
├── review          # 内容审核
└── admin           # 后台管理
```

## 数据模型概览

主要业务表包括：

- `user`：用户账号、认证状态和账号状态
- `category`：商品和失物分类
- `item`：二手商品
- `lost_found`：寻物/拾物信息
- `media`：图片和附件元数据
- `favorite`：收藏关系
- `conversation` / `message`：会话和消息
- `claim`：失物认领申请
- `report`：举报记录
- `review`：审核记录
- `audit_log`：管理员操作日志

详细字段和业务规则见：[需求说明书](outputs/拾光集市_二手交易与失物招领平台需求说明书.md)。

## 快速开始

当前仓库首先用于项目设计和后端实现准备。源码完成后，推荐按以下方式启动：

### 环境要求

- JDK 17+
- Maven 3.9+
- MySQL 8+
- Redis 7+
- RabbitMQ 3.12+（异步功能启用时）
- Docker Desktop（可选）

### 配置步骤

1. 创建 MySQL 数据库，例如 `shiguang_market`。
2. 执行 `sql/schema.sql` 和 `sql/data.sql` 初始化表结构与测试数据。
3. 复制配置文件并填写数据库、Redis、RabbitMQ 和文件存储信息。
4. 启动依赖服务。
5. 使用 Maven 启动 Spring Boot 应用。
6. 打开 Swagger UI 查看和调试接口。

示例命令：

```bash
mvn clean package
mvn spring-boot:run
```

> 当前仓库尚未包含完整后端源码时，上述命令属于目标启动方式；实际可运行命令会随代码目录和配置文件提交同步更新。

## API 设计方向

接口统一使用 `/api/v1` 前缀，并采用 REST/JSON 格式。

| 方法 | 路径 | 说明 |
|---|---|---|
| POST | `/api/v1/auth/register` | 用户注册 |
| POST | `/api/v1/auth/login` | 用户登录 |
| GET | `/api/v1/items` | 商品搜索列表 |
| POST | `/api/v1/items` | 发布商品 |
| GET | `/api/v1/items/{id}` | 商品详情 |
| POST | `/api/v1/items/{id}/status` | 修改商品状态 |
| GET | `/api/v1/lost-found` | 失物信息列表 |
| POST | `/api/v1/lost-found` | 发布失物信息 |
| POST | `/api/v1/lost-found/{id}/claims` | 发起认领 |
| POST | `/api/v1/reports` | 提交举报 |
| GET | `/api/v1/admin/reviews` | 获取审核队列 |
| POST | `/api/v1/admin/reviews/{id}/decision` | 处理审核 |

## 业务状态流转

### 二手商品

```text
草稿 -> 待审核 -> 在售 -> 交易中 -> 已售出
                         └───────> 已下架
待审核 -> 审核拒绝 -> 修改后重新提交
```

### 失物招领

```text
草稿 -> 待审核 -> 进行中 -> 处理中 -> 已找回 / 已归还
                         └──────────> 已关闭
```

## 后端项目重点

- 使用数据库索引优化关键词、分类、区域和时间筛选
- 使用 Redis 缓存热点详情，并处理缓存穿透、击穿和雪崩问题
- 使用 RabbitMQ 处理异步通知或数据导出任务
- 对收藏、认领、审核和消息发送实现幂等控制
- 使用统一异常、统一响应、参数校验和权限拦截
- 通过 JUnit、接口测试和压测验证核心接口
- 使用 Docker Compose 完成 MySQL、Redis、RabbitMQ 和应用部署

## 测试与验收目标

- 游客、普通用户、内容所有者和管理员权限测试通过
- 核心接口覆盖正常、参数错误、未登录和越权场景
- 重复收藏、重复认领和重复提交不会产生脏数据
- 核心列表接口在测试数据下具备稳定分页和排序能力
- 项目可以按照 README 在新环境中完成部署
- Swagger/OpenAPI、数据库脚本、测试数据和接口示例齐全

## 项目文档

- [需求说明书](outputs/拾光集市_二手交易与失物招领平台需求说明书.md)

## 开发路线

1. 完成用户认证、分类和基础数据库结构。
2. 完成二手商品和失物招领核心 CRUD。
3. 加入审核、举报、收藏和消息模块。
4. 加入 Redis 缓存、限流和 RabbitMQ 异步任务。
5. 补充单元测试、接口测试、日志和压测记录。
6. 使用 Docker Compose 部署并完善项目文档。

## 项目状态

当前阶段：**需求设计与后端实现准备**。

后续提交将逐步加入数据库脚本、Spring Boot 源码、接口测试、部署文件和演示数据。README 会随实际实现进度更新，已实现能力与规划能力会明确区分。

## 许可证

项目许可证待确定。用于学习、求职作品集和技术交流时，请勿直接用于真实生产环境，尤其是涉及身份信息、联系方式和交易纠纷的场景。
