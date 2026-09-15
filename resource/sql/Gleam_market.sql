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

-- 会话表
CREATE TABLE IF NOT EXISTS `conversation`(
    id           BIGINT AUTO_INCREMENT COMMENT '会话ID',
    item_id      BIGINT        NOT NULL        COMMENT '关联商品ID',
    user1_id     BIGINT        NOT NULL        COMMENT '参与者A（较小ID）',
    user2_id     BIGINT        NOT NULL        COMMENT '参与者B（较大ID）',
    last_message VARCHAR(500)                  COMMENT '最后一条消息',
    last_time    DATETIME                      COMMENT '最后消息时间',
    unread_count INT           NOT NULL DEFAULT 0 COMMENT '接收方未读数',
    create_time  DATETIME      NOT NULL        COMMENT '创建时间',
    update_time  DATETIME      NOT NULL        COMMENT '更新时间',
    deleted      TINYINT       DEFAULT 0       COMMENT '逻辑删除',
    PRIMARY KEY (id),
    FOREIGN KEY (item_id) REFERENCES `item`(id),
    FOREIGN KEY (user1_id) REFERENCES `user`(id),
    FOREIGN KEY (user2_id) REFERENCES `user`(id),
    UNIQUE KEY uk_item_users(item_id, user1_id, user2_id),
    INDEX idx_user1(user1_id),
    INDEX idx_user2(user2_id)
) ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4 COMMENT='会话表';

-- 消息表
CREATE TABLE IF NOT EXISTS `message`(
    id              BIGINT AUTO_INCREMENT COMMENT '消息ID',
    conversation_id BIGINT        NOT NULL        COMMENT '会话ID',
    sender_id       BIGINT        NOT NULL        COMMENT '发送者ID',
    receiver_id     BIGINT        NOT NULL        COMMENT '接收者ID',
    content         TEXT          NOT NULL        COMMENT '消息内容',
    type            VARCHAR(20)   NOT NULL DEFAULT 'TEXT' COMMENT '类型：TEXT/IMAGE',
    is_read         TINYINT(1)    NOT NULL DEFAULT 0 COMMENT '已读标志',
    create_time     DATETIME      NOT NULL        COMMENT '发送时间',
    PRIMARY KEY (id),
    FOREIGN KEY (conversation_id) REFERENCES `conversation`(id),
    FOREIGN KEY (sender_id) REFERENCES `user`(id),
    FOREIGN KEY (receiver_id) REFERENCES `user`(id),
    INDEX idx_conversation(conversation_id),
    INDEX idx_sender(sender_id),
    INDEX idx_receiver(receiver_id)
) ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4 COMMENT='消息表';

-- 收藏表
CREATE TABLE IF NOT EXISTS `favorite`(
    id          BIGINT   AUTO_INCREMENT  COMMENT '收藏ID',
    user_id     BIGINT   NOT NULL        COMMENT '用户ID',
    item_id     BIGINT   NOT NULL        COMMENT '商品ID',
    create_time DATETIME NOT NULL        COMMENT '收藏时间',
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES `user`(id),
    FOREIGN KEY (item_id) REFERENCES `item`(id),
    UNIQUE KEY uk_user_item(user_id, item_id),
    INDEX idx_user_id(user_id),
    INDEX idx_item_id(item_id)
) ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4 COMMENT='收藏表';

-- 举报表
CREATE TABLE IF NOT EXISTS `report`(
    id          BIGINT       AUTO_INCREMENT  COMMENT '举报ID',
    reporter_id BIGINT       NOT NULL        COMMENT '举报人ID',
    target_type VARCHAR(20)  NOT NULL        COMMENT '举报类型：ITEM/LOST_FOUND/USER',
    target_id   BIGINT       NOT NULL        COMMENT '被举报对象ID',
    reason      VARCHAR(50)  NOT NULL        COMMENT '举报原因',
    risk_level  VARCHAR(20)  NOT NULL DEFAULT 'MEDIUM' COMMENT '风险等级：LOW/MEDIUM/HIGH',
    description TEXT                         COMMENT '举报描述',
    status      VARCHAR(20)  NOT NULL DEFAULT 'PENDING' COMMENT '处理状态：PENDING/RESOLVED/DISMISSED',
    reviewer_id BIGINT                       COMMENT '处理人ID',
    review_note VARCHAR(500)                 COMMENT '处理备注',
    create_time DATETIME     NOT NULL        COMMENT '举报时间',
    update_time DATETIME     NOT NULL        COMMENT '处理时间',
    PRIMARY KEY (id),
    FOREIGN KEY (reporter_id) REFERENCES `user`(id),
    FOREIGN KEY (reviewer_id) REFERENCES `user`(id),
    INDEX idx_reporter(reporter_id),
    INDEX idx_target(target_type, target_id),
    INDEX idx_status(status)
) ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4 COMMENT='举报表';

-- 审核表
CREATE TABLE IF NOT EXISTS `review`(
                                       id            BIGINT       AUTO_INCREMENT  COMMENT '审核ID',
                                       target_type   VARCHAR(20)  NOT NULL        COMMENT '审核类型：ITEM/LOST_FOUND',
    target_id     BIGINT       NOT NULL        COMMENT '被审核对象ID',
    submitter_id  BIGINT       NOT NULL        COMMENT '提交人ID',
    reviewer_id   BIGINT                       COMMENT '审核人ID',
    status        VARCHAR(20)  NOT NULL DEFAULT 'PENDING' COMMENT '审核状态：PENDING/APPROVED/REJECTED',
    review_note   VARCHAR(500)                 COMMENT '审核备注',
    create_time   DATETIME     NOT NULL        COMMENT '提交时间',
    update_time   DATETIME     NOT NULL        COMMENT '审核时间',
    PRIMARY KEY (id),
    FOREIGN KEY (submitter_id) REFERENCES `user`(id),
    FOREIGN KEY (reviewer_id) REFERENCES `user`(id),
    INDEX idx_target(target_type, target_id),
    INDEX idx_status(status)
    ) ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4 COMMENT='审核表';