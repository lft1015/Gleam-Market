package com.shiguang.market.auth.service;

import com.shiguang.market.auth.dto.LoginRequest;
import com.shiguang.market.auth.dto.LoginResponse;
import com.shiguang.market.auth.dto.RegisterRequest;

/**
 * 认证服务接口
 *
 * @author gugu
 */

public interface AuthService {

    /**
     * 注册
     * @param request 注册请求
     */
    void register(RegisterRequest request);

    /**
     * 登录
     * @param request 登录请求
     * @return 登录响应
     */
    LoginResponse login(LoginRequest request);

    /**
     * 退出登录
     */
    void logout();
}