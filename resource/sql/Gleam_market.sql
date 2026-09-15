CREATE DATABASE IF NOT EXISTS `gleam_market`;

USE `gleam_market`;

-- 用户表
CREATE TABLE IF NOT EXISTS `user`(
    id BIGINT AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) UNIQUE NOT NULL COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    nickname VARCHAR(255) NOT NULL COMMENT '昵称',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    avatar VARCHAR(255) COMMENT '头像URL',
    role VARCHAR(20) NOT NULL DEFAULT 'USER' COMMENT '角色',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    update_time DATETIME NOT NULL COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username(username)
)ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4 COMMENT '用户表';

-- 商品表
CREATE TABLE IF NOT EXISTS `item`(
    id BIGINT AUTO_INCREMENT COMMENT '商品ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    title VARCHAR(255) NOT NULL COMMENT '商品标题',
    description TEXT COMMENT '商品描述',
    price DECIMAL(10,2) NOT NULL COMMENT '商品价格',
    original_price DECIMAL(10,2) COMMENT '商品原价',
    category VARCHAR(50) NOT NULL COMMENT '商品分类',
    status VARCHAR(20) NOT NULL DEFAULT 'ON_SALE' COMMENT '商品状态',
    images VARCHAR(2000) COMMENT '商品图片',
    view_count INT NOT NULL DEFAULT 0 COMMENT '商品查看次数',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    update_time DATETIME NOT NULL COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES `user`(id),
    INDEX idx_user_id(user_id),     -- 用户ID索引
    INDEX idx_category(category),   -- 商品分类索引
    INDEX idx_status(status)        -- 商品状态索引
)ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4 COMMENT '商品表';

-- 失物招领表
CREATE TABLE IF NOT EXISTS `lost_found`(
                                          id          BIGINT       AUTO_INCREMENT  COMMENT 'ID',
                                          user_id     BIGINT       NOT NULL         COMMENT '发布者ID',
                                          title       VARCHAR(100) NOT NULL         COMMENT '物品名称',
    description TEXT                          COMMENT '详细描述',
    type        VARCHAR(20)  NOT NULL         COMMENT '类型：LOST寻物/FOUND招领',
    location    VARCHAR(200) NOT NULL         COMMENT '丢失/捡到地点',
    lost_time   DATETIME     NOT NULL         COMMENT '丢失/捡到时间',
    contact     VARCHAR(100) NOT NULL         COMMENT '联系方式',
    images      VARCHAR(2000)                 COMMENT '图片',
    status      VARCHAR(20)  NOT NULL DEFAULT 'PENDING' COMMENT '状态：PENDING/CLAIMED/CLOSED',
    create_time DATETIME     NOT NULL         COMMENT '发布时间',
    update_time DATETIME     NOT NULL         COMMENT '修改时间',
    deleted     TINYINT      DEFAULT 0        COMMENT '逻辑删除',
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES `user`(id),
    INDEX idx_user_id (user_id),
    INDEX idx_type (type),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4 COMMENT='失物招领表';