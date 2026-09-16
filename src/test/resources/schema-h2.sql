DROP TABLE IF EXISTS audit_log;
DROP TABLE IF EXISTS announcement;
DROP TABLE IF EXISTS message;
DROP TABLE IF EXISTS conversation;
DROP TABLE IF EXISTS report;
DROP TABLE IF EXISTS favorite;
DROP TABLE IF EXISTS claim;
DROP TABLE IF EXISTS review;
DROP TABLE IF EXISTS lost_found;
DROP TABLE IF EXISTS item;
DROP TABLE IF EXISTS category;
DROP TABLE IF EXISTS `user`;

CREATE TABLE `user`(
    id BIGINT AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    nickname VARCHAR(255) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(20),
    avatar VARCHAR(255),
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    ban_until TIMESTAMP,
    create_time TIMESTAMP NOT NULL,
    update_time TIMESTAMP NOT NULL,
    deleted INT DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uk_username(username)
);

CREATE TABLE category(
    id BIGINT AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    sort_order INT DEFAULT 0,
    create_time TIMESTAMP NOT NULL,
    update_time TIMESTAMP NOT NULL,
    deleted INT DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uk_name(name)
);

CREATE TABLE item(
    id BIGINT AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    original_price DECIMAL(10,2),
    category VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ON_SALE',
    images VARCHAR(2000),
    view_count INT NOT NULL DEFAULT 0,
    create_time TIMESTAMP NOT NULL,
    update_time TIMESTAMP NOT NULL,
    deleted INT DEFAULT 0,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES `user`(id)
);

CREATE TABLE lost_found(
    id BIGINT AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    type VARCHAR(20) NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    location VARCHAR(255),
    lost_time TIMESTAMP,
    contact VARCHAR(255),
    images VARCHAR(2000),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING_REVIEW',
    create_time TIMESTAMP NOT NULL,
    update_time TIMESTAMP NOT NULL,
    deleted INT DEFAULT 0,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES `user`(id)
);

CREATE TABLE review(
    id BIGINT AUTO_INCREMENT,
    submitter_id BIGINT NOT NULL,
    reviewer_id BIGINT,
    target_type VARCHAR(50) NOT NULL,
    target_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    review_note TEXT,
    create_time TIMESTAMP NOT NULL,
    update_time TIMESTAMP NOT NULL,
    deleted INT DEFAULT 0,
    PRIMARY KEY (id),
    FOREIGN KEY (submitter_id) REFERENCES `user`(id)
);

CREATE TABLE claim(
    id BIGINT AUTO_INCREMENT,
    lost_found_id BIGINT NOT NULL,
    claimant_id BIGINT NOT NULL,
    message TEXT,
    contact VARCHAR(255),
    verification TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    reviewer_id BIGINT,
    review_note TEXT,
    create_time TIMESTAMP NOT NULL,
    update_time TIMESTAMP NOT NULL,
    deleted INT DEFAULT 0,
    PRIMARY KEY (id),
    FOREIGN KEY (lost_found_id) REFERENCES lost_found(id),
    FOREIGN KEY (claimant_id) REFERENCES `user`(id)
);

CREATE TABLE favorite(
    id BIGINT AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    item_id BIGINT NOT NULL,
    create_time TIMESTAMP NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES `user`(id),
    FOREIGN KEY (item_id) REFERENCES item(id)
);

CREATE TABLE conversation(
    id BIGINT AUTO_INCREMENT,
    item_id BIGINT,
    user1_id BIGINT NOT NULL,
    user2_id BIGINT NOT NULL,
    last_message TEXT,
    last_time TIMESTAMP,
    unread_count INT DEFAULT 0,
    create_time TIMESTAMP NOT NULL,
    update_time TIMESTAMP NOT NULL,
    deleted INT DEFAULT 0,
    PRIMARY KEY (id),
    FOREIGN KEY (user1_id) REFERENCES `user`(id),
    FOREIGN KEY (user2_id) REFERENCES `user`(id)
);

CREATE TABLE message(
    id BIGINT AUTO_INCREMENT,
    conversation_id BIGINT NOT NULL,
    sender_id BIGINT NOT NULL,
    receiver_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    type VARCHAR(20) DEFAULT 'TEXT',
    is_read INT DEFAULT 0,
    create_time TIMESTAMP NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (conversation_id) REFERENCES conversation(id),
    FOREIGN KEY (sender_id) REFERENCES `user`(id),
    FOREIGN KEY (receiver_id) REFERENCES `user`(id)
);

CREATE TABLE report(
    id BIGINT AUTO_INCREMENT,
    reporter_id BIGINT NOT NULL,
    target_type VARCHAR(50) NOT NULL,
    target_id BIGINT NOT NULL,
    reason TEXT NOT NULL,
    description TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    risk_level VARCHAR(20) DEFAULT 'LOW',
    reviewer_id BIGINT,
    review_note TEXT,
    create_time TIMESTAMP NOT NULL,
    update_time TIMESTAMP NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (reporter_id) REFERENCES `user`(id)
);

CREATE TABLE announcement(
    id BIGINT AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    publisher_id BIGINT,
    is_active TINYINT DEFAULT 1,
    create_time TIMESTAMP NOT NULL,
    update_time TIMESTAMP NOT NULL,
    deleted INT DEFAULT 0,
    PRIMARY KEY (id),
    FOREIGN KEY (publisher_id) REFERENCES `user`(id)
);

CREATE TABLE audit_log(
    id BIGINT AUTO_INCREMENT,
    admin_id BIGINT NOT NULL,
    action VARCHAR(50) NOT NULL,
    target_type VARCHAR(50),
    target_id BIGINT,
    detail TEXT,
    create_time TIMESTAMP NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (admin_id) REFERENCES `user`(id)
);