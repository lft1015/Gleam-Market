package com.shiguang.market.admin.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 修改用户状态请求
 *
 * @author gugu
 */
@Data
@Schema(description = "修改用户状态请求")
public class UserStatusRequest {

    @NotBlank(message = "状态不能为空")
    @Schema(description = "状态：ACTIVE/WARNED/MUTED/BANNED", example = "BANNED")
    private String status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "封禁截止时间（临时封禁时必填，永久封禁不传）")
    private LocalDateTime banUntil;

    @Schema(description = "操作说明")
    private String remark;
}