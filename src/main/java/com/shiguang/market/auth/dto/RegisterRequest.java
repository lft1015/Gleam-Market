package com.shiguang.market.auth.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 注册请求
 *
 * @author gugu
 */

@Data
public class RegisterRequest {
    // 用户名
    @NotBlank(message = "用户名不能为空")
    @Size(max = 50, message = "用户名不能超过50个字符")
    private String username;
    // 密码
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 72, message = "密码长度应为6到72位")
    private String password;
    // 昵称
    @NotBlank(message = "昵称不能为空")
    @Size(max = 30, message = "昵称不能超过30个字符")
    private String nickname;
}
