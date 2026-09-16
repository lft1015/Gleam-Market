INSERT INTO `user` (username, password, nickname, role, status, create_time, update_time)
VALUES ('testuser', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '测试用户', 'USER', 'ACTIVE', NOW(), NOW());

INSERT INTO `user` (username, password, nickname, role, status, create_time, update_time)
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '管理员', 'ADMIN', 'ACTIVE', NOW(), NOW());

INSERT INTO `user` (username, password, nickname, role, status, create_time, update_time)
VALUES ('seller', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '卖家', 'USER', 'ACTIVE', NOW(), NOW());

INSERT INTO category (name, sort_order, create_time, update_time)
VALUES ('电子产品', 1, NOW(), NOW());
INSERT INTO category (name, sort_order, create_time, update_time)
VALUES ('书籍', 2, NOW(), NOW());
INSERT INTO category (name, sort_order, create_time, update_time)
VALUES ('生活用品', 3, NOW(), NOW());

INSERT INTO item (user_id, title, description, price, original_price, category, status, images, view_count, create_time, update_time)
VALUES (1, '二手手机', '九成新', 999.00, 1999.00, '电子产品', 'ON_SALE', '["img1.jpg"]', 10, NOW(), NOW());
INSERT INTO item (user_id, title, description, price, category, status, view_count, create_time, update_time)
VALUES (3, '闲置键盘', '机械键盘', 199.00, '电子产品', 'ON_SALE', 5, NOW(), NOW());

INSERT INTO lost_found (user_id, type, title, description, location, lost_time, contact, status, create_time, update_time)
VALUES (1, 'LOST', '丢失钱包', '黑色钱包', '图书馆', NOW(), '张三 13800138000', 'IN_PROGRESS', NOW(), NOW());

INSERT INTO review (submitter_id, target_type, target_id, status, create_time, update_time)
VALUES (1, 'ITEM', 1, 'PENDING', NOW(), NOW());
INSERT INTO review (submitter_id, target_type, target_id, status, create_time, update_time)
VALUES (3, 'ITEM', 2, 'PENDING', NOW(), NOW());
INSERT INTO review (submitter_id, target_type, target_id, status, create_time, update_time)
VALUES (1, 'LOST_FOUND', 1, 'APPROVED', NOW(), NOW());

INSERT INTO report (reporter_id, target_type, target_id, reason, status, risk_level, create_time, update_time)
VALUES (3, 'ITEM', 1, '描述不符', 'PENDING', 'MEDIUM', NOW(), NOW());

INSERT INTO announcement (title, content, publisher_id, is_active, create_time, update_time)
VALUES ('系统公告', '欢迎使用拾光集市', 2, TRUE, NOW(), NOW());