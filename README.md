<div align="center">
  <h1>拾光集市 Gleam Market</h1>
  <p>面向校园与社区的二手交易 & 失物招领后端平台</p>
</div>

---

## 项目简介

拾光集市是一个功能完整的校园/社区二手交易与失物招领 **后端服务平台**，涵盖用户认证、商品发布审核、收藏私信、举报管理、管理员仪表盘等全链路业务。采用 **Spring Boot 3 + MyBatis-Plus** 构建，集成 **Redis 缓存 + RabbitMQ 异步消息**，支持 **Docker Compose 一键部署**。

启动后自动初始化管理员账号和全量测试数据，开箱即用。

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
- **状态管理**：`待审核 → 进行中 → 处理中 → 已找回 → 已归还 → 已关闭` / `已拒绝`
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
- **举报**：提交举报（含原因 + 风险等级 `LOW/MEDIUM/HIGH`）→ 管理员处理 → MQ 通知举报人

### 管理员
- **仪表盘**：用户总数 / 商品总数 / 订单总数 / 举报总数
- **用户管理**：列表查询 / 状态变更（`ACTIVE` / `WARNED` / `MUTED` / `BANNED`）
- **审核管理**：审核列表 / 审核决定
- **公告管理**：发布 / 编辑 / 删除 / 公开列表
- **分类管理**：分类 CRUD
- **状态拦截**：`UserStatusFilter` 自动拦截禁言/封禁用户的非公开请求

## 数据库设计

共 **12 张核心表**：

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

| 场景 | 交换机 | 路由键 | 重试策略 |
|------|--------|--------|----------|
| 审核通知 | `gleam.notification.exchange` | `gleam.review` | 3 次（3s → 6s → 12s）→ 死信队列 |
| 举报通知 | `gleam.notification.exchange` | `gleam.report` | 3 次（3s → 6s → 12s）→ 死信队列 |

> 死信队列 `gleam.dead` 兜底所有处理失败的消息。

---

## 部署

### 前置要求

| 依赖 | 最低版本 |
|------|----------|
| Docker | 20.10+ |
| Docker Compose | 2.0+ |
| Git | 任意版本 |

> 无需本地安装 JDK / MySQL / Redis / RabbitMQ，全都由 Docker 提供。

### 一键部署

```bash
# 1. 克隆项目
git clone https://github.com/lft1015/Gleam-Market.git
cd gleam-market

# 2. 配置环境变量（可选，不配置则使用默认值）
cp .env.example .env
# 编辑 .env 修改密码等敏感信息

# 3. 启动全部服务
docker compose up -d --build
```

### 启动流程

Docker Compose 会按依赖顺序自动启动以下 5 个容器：

| 容器 | 服务 | 端口 | 依赖 | 说明 |
|------|------|------|------|------|
| `gleam-mysql` | MySQL 8.0 | - | - | 首次启动自动执行 `Gleam_market.sql` 建表 |
| `gleam-redis` | Redis 7 | - | - | 缓存 + 浏览量计数 |
| `gleam-rabbitmq` | RabbitMQ 3.12 | 15672 | - | 异步通知 + 管理界面 |
| `gleam-upload-init` | 初始化 | - | - | 创建上传目录（一次性任务） |
| `gleam-app` | Spring Boot | 8080 | 以上全部 | 后端 API 服务，启动后自动播种测试数据 |

> MySQL / Redis 端口默认不暴露到宿主机，仅容器间通信，更安全。

### 环境变量

在 `.env` 文件中配置（键 = `.env.example` 中定义的变量）：

| 变量 | 默认值 | 说明 |
|------|--------|------|
| `APP_PORT` | `8080` | 应用对外的 HTTP 端口 |
| `MYSQL_DATABASE` | `gleam_market` | 数据库名 |
| `MYSQL_ROOT_PASSWORD` | `liao2004` | MySQL root 密码，**生产必须修改** |
| `RABBITMQ_MANAGEMENT_PORT` | `15672` | RabbitMQ 管理界面端口 |
| `RABBITMQ_USER` | `gleam` | RabbitMQ 用户名 |
| `RABBITMQ_PASSWORD` | `gleam-market` | RabbitMQ 密码，**生产必须修改** |
| `APP_ADMIN_USERNAME` | `admin` | 自动创建的管理员用户名 |
| `APP_ADMIN_PASSWORD` | `admin123` | 自动创建的管理员密码，**生产必须修改** |
| `APP_ADMIN_NICKNAME` | `平台管理员` | 管理员昵称 |

### 验证部署

```bash
# 查看全部容器状态（都应显示 healthy 或 Up）
docker compose ps

# 查看应用日志（出现 Started GleamMarketApplication 即启动成功）
docker compose logs -f app

# 测试 API
curl http://localhost:8080/api/v1/items?page=1&size=5
curl http://localhost:8080/api/v1/admin/dashboard -H "Authorization: Bearer <token>"
```

### 启动后访问

| 服务 | 地址 | 用户 | 密码 |
|------|------|------|------|
| API 文档 (Knife4j) | http://localhost:8080/api/v1/doc.html | - | - |
| RabbitMQ 管理界面 | http://localhost:15672 | `gleam` | `gleam-market` |

### 自动初始化的数据

启动后 `AdminBootstrap` 和 `DataSeeder` 会自动播种：

| 数据 | 数量 | 说明 |
|------|------|------|
| 管理员账号 | 1 | `APP_ADMIN_USERNAME` / `APP_ADMIN_PASSWORD` 指定的管理员 |
| 测试用户 | 3 | `testuser` / `testadmin` / `testsuperadmin`（密码均为 `gugu`） |
| 商品 | 13 | 覆盖 ON_SALE ×7 / DRAFT / PENDING_REVIEW / REJECTED / TRADING / SOLD / OFF_SHELF |
| 失物招领 | 11 | 覆盖 PENDING ×3 / PENDING_REVIEW / REJECTED / IN_PROGRESS / PROCESSING ×2 / FOUND / RETURNED / CLOSED |
| 认领记录 | 3 | PENDING / APPROVED / REJECTED |
| 举报记录 | 5 | 含 LOW / MEDIUM / HIGH 三级风险 |
| 审核记录 | 6 | ITEM ×3 + LOST_FOUND ×3 |
| 私信会话 | 2 | 含 9 条消息（已读 + 未读） |
| 收藏记录 | 6 | 3 个用户的收藏 |
| 公告 | 4 | 3 条有效 + 1 条已过期 |

### 本地开发部署

适用于需要断点调试或修改源码的场景：

```bash
# 1. 只启动中间件（MySQL / Redis / RabbitMQ）
docker compose up -d mysql redis rabbitmq

# 2. 确认中间件就绪
docker compose ps

# 3. IDEA 中运行 GleamMarketApplication.main()
#    或执行：./mvnw spring-boot:run

# 4. 停止全部服务
docker compose down
```

> 本地开发时需修改 `application.yml` 中的 Redis/MySQL/RabbitMQ 主机地址为 `localhost`。

### 常用运维命令

```bash
# 查看日志
docker compose logs -f app                  # 应用日志
docker compose logs -f mysql                # 数据库日志

# 重启单个服务
docker compose restart app

# 停止全部服务
docker compose down

# 停止并清除所有数据（数据库 + Redis + RabbitMQ 数据全部删除）
docker compose down -v

# 重新构建并启动（源码修改后）
docker compose up -d --build

# 进入 MySQL 命令行
docker exec -it gleam-mysql mysql -uroot -p

# 进入 Redis 命令行
docker exec -it gleam-redis redis-cli
```

### 生产部署建议

1. **修改所有默认密码**：编辑 `.env` 修改 `MYSQL_ROOT_PASSWORD`、`RABBITMQ_PASSWORD`、`APP_ADMIN_PASSWORD`
2. **关闭 API 文档**：在 `application.yml` 中设置 Knife4j `enable: false`
3. **配置 HTTPS**：前端部署 Nginx + SSL 证书，反向代理到后端 8080 端口
4. **数据持久化**：Docker Volume 自动持久化，建议额外配置定期数据库备份
5. **日志收集**：将日志挂载到宿主机或接入 ELK / Loki 等日志平台

---

## 安全设计

- **认证**：JWT Token，24h 有效期，退出时写入 Redis 黑名单
- **授权**：Spring Security 角色控制（`USER` / `ADMIN` / `SUPER_ADMIN`）
- **密码**：BCrypt 加密存储
- **状态拦截**：`UserStatusFilter` 自动拦截禁言/封禁用户的非公开请求
- **统一异常**：`GlobalExceptionHandler` 全局异常捕获，统一返回格式

## 项目亮点

1. **完整业务闭环**：发布 → 审核 → 上架 → 交易 → 收藏/私信，失物 → 认领 → 归还
2. **缓存策略**：Redis 缓存 + 空值防穿透 + 浏览量异步计数批量同步
3. **消息驱动**：RabbitMQ 异步通知 + 死信队列 + 指数退避重试
4. **容器化部署**：Dockerfile 多阶段构建 + Compose 一键编排全部依赖
5. **开箱即用**：启动后自动建表 + 创建管理员 + 播种全量测试数据
6. **代码规范**：Controller → Service → Mapper 标准三层架构，状态常量化

## License

MIT

---

<p align="center">Made with ❤️ by gugu</p>