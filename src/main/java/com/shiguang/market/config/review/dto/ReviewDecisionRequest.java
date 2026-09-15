package com.shiguang.market.config.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 审核决定请求
 *
 * @author gugu
 */
@Data
@Schema(description = "审核决定请求")
public class ReviewDecisionRequest {

    @NotBlank(message = "审核结果不能为空")
    @Schema(description = "审核结果：APPROVED/REJECTED")
    private String status;

    @Schema(description = "审核备注")
    private String reviewNote;
}