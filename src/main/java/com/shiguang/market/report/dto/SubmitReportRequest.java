package com.shiguang.market.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 提交举报请求DTO
 *
 * @author gugu
 */
@Data
@Schema(description = "提交举报请求")
public class SubmitReportRequest {

    @NotBlank(message = "举报类型不能为空")
    @Schema(description = "举报类型：ITEM/LOST_FOUND/USER")
    private String targetType;

    @NotNull(message = "被举报对象ID不能为空")
    @Schema(description = "被举报对象ID")
    private Long targetId;

    @NotBlank(message = "举报原因不能为空")
    @Schema(description = "举报原因")
    private String reason;

    @Schema(description = "风险等级")
    private String riskLevel;

    @Schema(description = "举报描述")
    private String description;
}