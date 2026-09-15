package com.shiguang.market.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 修改资料请求类
 *
 * @author gugu
 */
@Data
@Schema(description = "修改资料请求类")
public class UpdateProfileRequest {

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "头像URL")
    private String avatar;
}