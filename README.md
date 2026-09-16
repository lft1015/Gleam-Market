<div align="center">
  <h1>拾光集市 Gleam Market</h1>
  <p>面向校园与社区的二手交易 & 失物招领后端平台</p>
</div>

---

## 项目简介

拾光集市是一个功能完整的校园/社区二手交易与失物招领 **后端服务平台**，涵盖用户认证、商品发布审核、收藏私信、举报管理、管理员仪表盘等全链路业务。采用 **Spring Boot 3 + MyBatis-Plus** 构建，集成 **Redis 缓存 + RabbitMQ 异步消息**，支持 **Docker Compose 一键部署**。

## 技术栈

| 层次 | 技术 | 说明 |
|------|------|------|
| 语言 | Java 17 | 长期支持版本 |
| 框架 | Spring Boot 3.0.2 | 核心框架 |
| 安全 | Spring Security + JWT | 无状态认证 + 角色权限 |
| ORM | MyBatis-Plus 3.5.5 | 增强 MyBatis，Lambda 查询 |
| 数据库 | MySQL 8 | 关系型存储 |
| 缓存 | Redis 7 | Token 黑名单 + 商品缓存 + 浏览量计数 |
| 消息队列 | RabbitMQ 3.12 | 审核/举报异步通知 |
| API 文档 | Knife4j (Swagger) | 在线调试 |
| 工具 | Hutool + Lombok | 工具类增强 |
| 容器化 | Docker + Docker Compose | 一键部署 |

## 项目结构

```
src/main/java/com/shiguang/market/
├── admin/            # 管理员模块（仪表盘 / 用户管理 / 分类 / 公告 / 审核日志）
├── auth/             # 认证模块（注册 / 登录 / 退出 / JWT 过滤器）
├── claim/            # 认领模块（失物认领提交 / 审批）
├── common/           # 公共组件（统一响应 / 异常处理 / Redis Key 前缀）
├── config/           # 配置（Security / MyBatis-Plus / Redis / RabbitMQ / Knife4j）
├── favorite/         # 收藏模块（添加 / 取消 / 检查 / 列表）
├── item/             # 商品模块（发布 / 列表 / 详情 / 编辑 / 状态流转）
├── lostfound/        # 失物招领模块（发布 / 列表 / 详情 / 状态流转）
├── message/          # 消息模块（私信 / 会话 / 已读 / MQ 通知）
├── report/           # 举报模块（提交 / 管理 / 处理 / 风险等级）
├── review/           # 审核模块（审核记录 / 通过 / 拒绝）
└── user/             # 用户模块（资料查看 / 编辑）
```

## 功能模块

### 用户 & 认证
- **注册**：用户名 + 密码 + 昵称（BCrypt 加密）
- **登录**：返回 JWT Token（24h 有效期）
- **退出**：Token 写入 Redis 黑名单（TTL = 剩余有效时长）
- **用户资料**：查看 / 编辑个人信息

### 商品交易
- **发布**：商品信息提交后自动创建审核记录
- **状态流转**：`草稿 → 待审核 → 在售 → 交易中 → 已售出` / `已拒绝` / `已下架`
- **浏览优化**：Redis 缓存商品详情（10min TTL）+ 防穿透空值标记
- **浏览量**：`INCR` 异步计数 → 定时任务每 5 分钟批量同步 MySQL
- **搜索筛选**：关键字 / 分类 / 价格区间 / 状态

### 失物招领
- **发布**：失物 / 招领信息（含图片 + 地点 + 时间 + 联系人）
- **状态管理**：`待审核 → 进行中 → 已找回 → 已归还 → 已关闭` / `已拒绝`
- **认领**：提交认领申请 → 管理员审批（通过 / 拒绝）
- **筛选查询**：类型 / 关键字 / 地点 / 时间范围

### 审核系统
- **自动创建**：商品 / 失物招领发布时自动创建审核记录
- **审核决策**：通过 → 更新目标状态；拒绝 → 填写原因
- **异步通知**：审核结果通过 RabbitMQ 投递通知消息

### 消息 & 私信
- **发送消息**：用户间一对一私信
- **会话列表**：按最近消息排序
- **消息已读**：批量标记已读
- **异步通知**：MQ 消费者监听审核 / 举报通知（预留推送/短信扩展点）

### 收藏 & 举报
- **收藏**：添加 / 取消 / 检查状态 / 计数 / 分页列表
- **举报**：提交举报（含原因 + 风险等级）→ 管理员处理 → MQ 通知举报人

### 管理员
- **仪表盘**：用户总数 / 商品总数 / 订单总数 / 举报总数
- **用户管理**：列表查询 / 状态变更（ACTIVE / WARNED / MUTED / BANNED）
- **审核管理**：审核列表 / 审核决定
- **公告管理**：发布 / 编辑 / 删除 / 公开列表
- **分类管理**：分类 CRUD
- **状态拦截**：用户状态过滤器自动拦截禁言/封禁用户请求

## 数据库设计

共 **11 张核心表**：

| 表名 | 说明 |
|------|------|
| `user` | 用户表（含角色 + 状态） |
| `item` | 商品表（含 7 种状态流转） |
| `lost_found` | 失物招领表（含 8 种状态流转） |
| `review` | 审核记录表 |
| `claim` | 认领申请表 |
| `favorite` | 收藏关联表 |
| `message` | 私信消息表 |
| `conversation` | 会话表 |
| `report` | 举报记录表 |
| `category` | 分类表 |
| `announcement` | 公告表 |
| `audit_log` | 操作日志表 |

## API 接口一览

| 模块 | 方法 | 端点 | 说明 |
|------|------|------|------|
| **认证** | POST | `/api/v1/auth/register` | 用户注册 |
| | POST | `/api/v1/auth/login` | 用户登录 |
| | POST | `/api/v1/auth/logout` | 退出登录 |
| **用户** | GET | `/api/v1/user/profile` | 查看个人资料 |
| | PUT | `/api/v1/user/profile` | 编辑个人资料 |
| **商品** | POST | `/api/v1/items` | 发布商品 |
| | GET | `/api/v1/items` | 分页搜索商品 |
| | GET | `/api/v1/items/{id}` | 商品详情 |
| | PUT | `/api/v1/items/{id}` | 编辑商品 |
| | PUT | `/api/v1/items/{id}/status` | 修改商品状态 |
| **失物** | POST | `/api/v1/lost-found` | 发布失物招领 |
| | GET | `/api/v1/lost-found` | 分页搜索失物招领 |
| | GET | `/api/v1/lost-found/{id}` | 失物招领详情 |
| | PUT | `/api/v1/lost-found/{id}/status` | 修改状态 |
| **认领** | POST | `/api/v1/claims` | 提交认领申请 |
| | GET | `/api/v1/claims/my` | 我的认领列表 |
| | PUT | `/api/v1/claims/{id}/approve` | 通过认领 |
| | PUT | `/api/v1/claims/{id}/reject` | 拒绝认领 |
| **收藏** | POST | `/api/v1/favorites` | 添加收藏 |
| | DELETE | `/api/v1/favorites/{itemId}` | 取消收藏 |
| | GET | `/api/v1/favorites/check/{itemId}` | 检查是否收藏 |
| | GET | `/api/v1/favorites/count/{itemId}` | 收藏数量 |
| | GET | `/api/v1/favorites` | 我的收藏列表 |
| **消息** | POST | `/api/v1/messages` | 发送消息 |
| | GET | `/api/v1/messages/conversations` | 会话列表 |
| | GET | `/api/v1/messages/{conversationId}` | 消息列表 |
| | PUT | `/api/v1/messages/{conversationId}/read` | 标记已读 |
| **审核** | GET | `/api/v1/admin/reviews` | 审核列表 |
| | PUT | `/api/v1/admin/reviews/{id}/decide` | 审核决定 |
| **举报** | POST | `/api/v1/reports` | 提交举报 |
| | GET | `/api/v1/reports/my` | 我的举报 |
| | GET | `/api/v1/admin/reports` | 举报管理列表 |
| | PUT | `/api/v1/admin/reports/{id}/handle` | 处理举报 |
| **管理员** | GET | `/api/v1/admin/dashboard` | 仪表盘 |
| | GET | `/api/v1/admin/users` | 用户列表 |
| | PUT | `/api/v1/admin/users/{id}/status` | 修改用户状态 |
| | CRUD | `/api/v1/admin/categories` | 分类管理 |
| | CRUD | `/api/v1/admin/announcements` | 公告管理 |
| | GET | `/api/v1/announcements` | 公告公开列表 |

> API 文档启动后访问：http://localhost:8080/api/v1/doc.html

## Redis 应用场景

| 场景 | Key 格式 | TTL | 面试亮点 |
|------|----------|-----|----------|
| Token 黑名单 | `token:blacklist:{token}` | JWT 剩余有效时长 | 无状态 JWT 主动失效 |
| 商品缓存 | `item:detail:{id}` | 10 分钟 | 空值 `__NULL__` 防缓存穿透 |
| 浏览量 | `item:view:{id}` | 永久（定时清零） | 异步计数 + 5 分钟批量写 MySQL |

## RabbitMQ 消息设计

| 场景 | 交换机 | 路由键 | 死信队列 |
|------|--------|--------|----------|
| 审核通知 | `gleam.notification.exchange` | `gleam.review` | `gleam.dead>3 次重试` |
| 举报通知 | `gleam.notification.exchange` | `gleam.report` | `gleam.dead>3 次重试` |

> 重试策略：3 次（3s → 6s → 12s），最终落入死信队列兜底

## 快速开始

### 前置要求

- Docker & Docker Compose
- JDK 17+ (本地开发)

### Docker 一键部署（推荐）

```bash
git clone https://github.com/your-username/gleam-market.git
cd gleam-market
docker compose up -d --build
```

首次启动会自动：
1. 拉取 **MySQL 8 + Redis 7 + RabbitMQ 3.12** 镜像
2. 初始化数据库（自动执行 `Gleam_market.sql` 建表）
3. Maven 编译打包 Spring Boot 应用
4. 按依赖顺序启动所有服务

启动后访问：

| 服务 | 地址 | 凭据 |
|------|------|------|
| API 文档 (Knife4j) | http://localhost:8080/api/v1/doc.html | - |
| RabbitMQ 管理界面 | http://localhost:15672 | guest / guest |

### 本地开发

1. 确保本地已安装并启动 **MySQL 8**、**Redis**、**RabbitMQ**

2. 导入数据库：
```bash
mysql -u root -p < resource/sql/Gleam_market.sql
```

3. 修改 `application.yml` 中的数据库密码等连接信息

4. 启动项目：
```bash
./mvnw spring-boot:run
```

## 安全设计

- **认证**：JWT Token，24h 有效期，退出时写入 Redis 黑名单
- **授权**：Spring Security 角色控制（`USER` / `ADMIN`）
- **密码**：BCrypt 加密存储
- **状态拦截**：`UserStatusFilter` 自动拦截禁言/封禁用户的非公开请求
- **统一异常**：`GlobalExceptionHandler` 全局异常捕获，统一返回格式

## 项目亮点

1. **完整业务闭环**：发布 → 审核 → 上架 → 交易 → 评价，失物 → 认领 → 归还
2. **缓存策略**：Redis 缓存 + 空值防穿透 + 浏览量异步计数批量同步
3. **消息驱动**：RabbitMQ 异步通知 + 死信队列 + 指数退避重试
4. **Docker 化**：Dockerfile 多阶段构建 + Compose 一键编排全部依赖
5. **代码规范**：Controller → Service → Mapper 标准三层架构，状态常量化

## License

MIT

---

<p align="center">Made with ❤️ by gugu</p>
