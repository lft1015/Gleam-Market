package com.shiguang.market.admin.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shiguang.market.user.entity.User;
import com.shiguang.market.user.mapper.UserMapper;
import com.shiguang.market.admin.entity.Category;
import com.shiguang.market.admin.mapper.CategoryMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
@Order(1)
public class AdminBootstrap implements ApplicationRunner {
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final CategoryMapper categoryMapper;
    @Value("${app.bootstrap-admin.username:}") private String username;
    @Value("${app.bootstrap-admin.password:}") private String password;
    @Value("${app.bootstrap-admin.nickname:平台管理员}") private String nickname;

    @Override
    public void run(ApplicationArguments args) {
        log.info("[AdminBootstrap] 开始执行数据初始化...");
        try {
            seedCategories();
            if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
                log.warn("[AdminBootstrap] 未配置 APP_ADMIN_USERNAME / APP_ADMIN_PASSWORD，跳过管理员创建");
                return;
            }
            User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
            if (user == null) {
                user = new User();
                user.setUsername(username);
                user.setNickname(nickname);
                user.setPassword(passwordEncoder.encode(password));
                user.setRole("ADMIN");
                user.setStatus("ACTIVE");
                user.setCreateTime(LocalDateTime.now());
                log.info("[AdminBootstrap] 创建管理员用户: {}", username);
            } else {
                user.setRole("ADMIN");
                user.setPassword(passwordEncoder.encode(password));
                log.info("[AdminBootstrap] 更新管理员用户: {}", username);
            }
            user.setUpdateTime(LocalDateTime.now());
            if (user.getId() == null) userMapper.insert(user); else userMapper.updateById(user);
            log.info("[AdminBootstrap] 数据初始化完成");
        } catch (Exception e) {
            log.error("[AdminBootstrap] 数据初始化异常", e);
        }
    }

    private void seedCategories() {
        if (categoryMapper.selectCount(null) > 0) {
            log.info("[AdminBootstrap] 分类已存在，跳过播种");
            return;
        }
        String[] names = {"数码电子", "图书教材", "生活用品", "服饰鞋包", "运动户外", "文具办公", "其他"};
        for (int i = 0; i < names.length; i++) {
            Category category = new Category();
            category.setName(names[i]);
            category.setSortOrder(i + 1);
            category.setCreateTime(LocalDateTime.now());
            category.setUpdateTime(LocalDateTime.now());
            categoryMapper.insert(category);
        }
        log.info("[AdminBootstrap] 播种 {} 个分类", names.length);
    }
}