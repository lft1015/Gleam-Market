package com.shiguang.market.auth.dto;

import lombok.Data;

/**
 * 登录请求
 *
 * @author gugu
 */
@Data
public class LoginRequest {
    // 用户名
    private String username;
    // 密码
    private String password;
}
