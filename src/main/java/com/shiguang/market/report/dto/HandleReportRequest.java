package com.shiguang.market.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 处理举报请求DTO
 *
 * @author gugu
 */

@Data
@Schema(description = "处理举报请求")
public class HandleReportRequest {

    @NotBlank(message = "处理结果不能为空")
    @Schema(description = "处理结果：RESOLVED/DISMISSED")
    private String status;

    @Schema(description = "处理备注")
    private String reviewNote;
}