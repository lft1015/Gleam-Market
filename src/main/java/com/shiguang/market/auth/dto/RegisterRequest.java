package com.shiguang.market.auth.dto;

import lombok.Data;

/**
 * 注册请求
 *
 * @author gugu
 */

@Data
public class RegisterRequest {
    // 用户名
    private String username;
    // 密码
    private String password;
    // 昵称
    private String nickname;
}
