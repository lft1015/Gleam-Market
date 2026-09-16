package com.shiguang.market.admin.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shiguang.market.user.entity.User;
import com.shiguang.market.user.mapper.UserMapper;
import com.shiguang.market.admin.entity.Category;
import com.shiguang.market.admin.mapper.CategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

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
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) return;
        seedCategories();
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (user == null) {
            user = new User();
            user.setUsername(username);
            user.setNickname(nickname);
            user.setPassword(passwordEncoder.encode(password));
            user.setRole("ADMIN");
            user.setStatus("ACTIVE");
            user.setCreateTime(LocalDateTime.now());
        } else {
            user.setRole("ADMIN");
            user.setPassword(passwordEncoder.encode(password));
        }
        user.setUpdateTime(LocalDateTime.now());
        if (user.getId() == null) userMapper.insert(user); else userMapper.updateById(user);
    }

    private void seedCategories() {
        if (categoryMapper.selectCount(null) > 0) return;
        String[] names = {"数码电子", "图书教材", "生活用品", "服饰鞋包", "运动户外", "文具办公", "其他"};
        for (int i = 0; i < names.length; i++) {
            Category category = new Category();
            category.setName(names[i]);
            category.setSortOrder(i + 1);
            category.setCreateTime(LocalDateTime.now());
            category.setUpdateTime(LocalDateTime.now());
            categoryMapper.insert(category);
        }
    }
}