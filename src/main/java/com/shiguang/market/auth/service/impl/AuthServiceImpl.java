package com.shiguang.market.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shiguang.market.auth.dto.LoginRequest;
import com.shiguang.market.auth.dto.LoginResponse;
import com.shiguang.market.auth.dto.RegisterRequest;
import com.shiguang.market.auth.service.AuthService;
import com.shiguang.market.auth.util.JwtUtils;
import com.shiguang.market.common.BusinessException;
import com.shiguang.market.user.entity.User;
import com.shiguang.market.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 认证服务实现类
 *
 * @author gugu
 */

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    /**
     * 注册用户
     * @param request 注册请求参数
     */
    @Override
    public void register(RegisterRequest request) {
        // 1. 查用户名是否重复
        User exist = userMapper.selectOne(
                new LambdaQueryWrapper<User>()
                        .eq(User::getUsername, request.getUsername())
        );
        if (exist != null) {
            throw new BusinessException(400, "用户名已存在");
        }

        // 2. 构建 User 对象
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getNickname());
        user.setRole("USER");
        user.setStatus("ACTIVE");
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        // 3. 插入数据库
        userMapper.insert(user);
    }

    /**
     * 登录用户
     * @param request 登录请求参数
     * @return 登录响应
     */
    @Override
    public LoginResponse login(LoginRequest request) {
        // 1. 查用户
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>()
                        .eq(User::getUsername, request.getUsername())
        );
        if (user == null) {
            throw new BusinessException(401, "用户名或密码错误");
        }

        // 2. 校验密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(401, "用户名或密码错误");
        }

        // 3. 生成 Token + 返回
        String token = jwtUtils.generateToken(user.getId(), user.getRole());
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUserId(user.getId());
        response.setNickname(user.getNickname());
        response.setRole(user.getRole());
        return response;
    }
}
