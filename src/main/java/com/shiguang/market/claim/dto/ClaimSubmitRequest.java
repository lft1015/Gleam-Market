package com.shiguang.market.claim.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "提交认领申请请求")
public class ClaimSubmitRequest {

    @NotBlank(message = "认领说明不能为空")
    @Schema(description = "认领说明")
    private String message;

    @NotBlank(message = "联系方式不能为空")
    @Schema(description = "联系方式")
    private String contact;

    @Schema(description = "凭证信息")
    private String verification;
}