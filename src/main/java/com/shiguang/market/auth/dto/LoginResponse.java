package com.shiguang.market.auth.dto;

import lombok.Data;

/**
 * 登录响应
 *
 * @author gugu
 */

@Data
public class LoginResponse {
    // Token
    private String token;

    // 用户ID
    private Long userId;

    // 昵称
    private String nickname;

    // 角色
    private String role;
}
