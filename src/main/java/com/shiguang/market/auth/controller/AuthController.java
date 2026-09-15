package com.shiguang.market.auth.controller;

import com.shiguang.market.auth.dto.LoginRequest;
import com.shiguang.market.auth.dto.LoginResponse;
import com.shiguang.market.auth.dto.RegisterRequest;
import com.shiguang.market.auth.service.AuthService;
import com.shiguang.market.common.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证控制器
 * 提供用户注册和登录的接口
 *
 * @author gugu
 */

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 注册
     * @param request 注册请求
     * @return 注册响应
     */
    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return Result.ok();
    }

    /**
     * 登录
     * @param request 登录请求
     * @return 登录响应
     */
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return Result.ok(authService.login(request));
    }
}