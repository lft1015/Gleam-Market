package com.shiguang.market.auth.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shiguang.market.auth.dto.LoginRequest;
import com.shiguang.market.auth.dto.LoginResponse;
import com.shiguang.market.auth.dto.RegisterRequest;
import com.shiguang.market.auth.util.JwtUtils;
import com.shiguang.market.common.BusinessException;
import com.shiguang.market.common.RedisKeyPrefix;
import com.shiguang.market.user.entity.User;
import com.shiguang.market.user.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("认证服务单元测试")
class AuthServiceTest {

    @Mock private UserMapper userMapper;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtUtils jwtUtils;
    @Mock private RedisTemplate<String, Object> redisTemplate;
    @Mock private ValueOperations<String, Object> valueOperations;

    @InjectMocks
    private com.shiguang.market.auth.service.impl.AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    @DisplayName("注册成功 - 用户名不重复")
    void register_success() {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("newuser");
        req.setPassword("123456");
        req.setNickname("新用户");

        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        when(passwordEncoder.encode("123456")).thenReturn("$2a$10$encoded");
        when(userMapper.insert(any(User.class))).thenReturn(1);

        assertDoesNotThrow(() -> authService.register(req));
        verify(userMapper).insert(any(User.class));
    }

    @Test
    @DisplayName("注册失败 - 用户名已存在")
    void register_duplicateUsername_throwsException() {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("existing");
        req.setPassword("123456");
        req.setNickname("重复用户");

        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(new User());

        BusinessException ex = assertThrows(BusinessException.class, () -> authService.register(req));
        assertEquals(400, ex.getCode());
        assertEquals("用户名已存在", ex.getMessage());
        verify(userMapper, never()).insert(any());
    }

    @Test
    @DisplayName("登录成功 - 用户名密码正确")
    void login_success() {
        LoginRequest req = new LoginRequest();
        req.setUsername("testuser");
        req.setPassword("123456");

        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("$2a$10$encoded");
        user.setNickname("测试");
        user.setRole("USER");

        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(user);
        when(passwordEncoder.matches("123456", user.getPassword())).thenReturn(true);
        when(jwtUtils.generateToken(1L, "USER")).thenReturn("jwt-token-xxx");

        LoginResponse resp = authService.login(req);

        assertNotNull(resp);
        assertEquals("jwt-token-xxx", resp.getToken());
        assertEquals(1L, resp.getUserId());
        assertEquals("测试", resp.getNickname());
        assertEquals("USER", resp.getRole());
    }

    @Test
    @DisplayName("登录失败 - 密码错误")
    void login_wrongPassword_throwsException() {
        LoginRequest req = new LoginRequest();
        req.setUsername("testuser");
        req.setPassword("wrong");

        User user = new User();
        user.setPassword("$2a$10$encoded");

        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(user);
        when(passwordEncoder.matches("wrong", user.getPassword())).thenReturn(false);

        BusinessException ex = assertThrows(BusinessException.class, () -> authService.login(req));
        assertEquals(401, ex.getCode());
    }

    @Test
    @DisplayName("退出成功 - Token 写入黑名单")
    void logout_success() {
        String token = "bearer-token-xxx";
        when(jwtUtils.getRemainingTtlSeconds(token)).thenReturn(3600L);

        assertDoesNotThrow(() -> authService.logout(token));
        verify(valueOperations).set(
                eq(RedisKeyPrefix.TOKEN_BLACKLIST + token),
                eq("1"),
                eq(3600L),
                eq(TimeUnit.SECONDS));
    }

    @Test
    @DisplayName("退出 - 空 Token 无操作")
    void logout_emptyToken_noOp() {
        assertDoesNotThrow(() -> authService.logout(""));
        verify(valueOperations, never()).set(anyString(), any(), anyLong(), any());
    }
}