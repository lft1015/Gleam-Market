package com.shiguang.market.report.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 举报响应DTO
 *
 * @author gugu
 */

@Data
@Schema(description = "举报响应")
public class ReportResponse {

    @Schema(description = "举报ID")
    private Long id;

    @Schema(description = "举报人ID")
    private Long reporterId;

    @Schema(description = "举报类型")
    private String targetType;

    @Schema(description = "被举报对象ID")
    private Long targetId;

    @Schema(description = "举报原因")
    private String reason;

    @Schema(description = "风险等级")
    private String riskLevel;

    @Schema(description = "举报描述")
    private String description;

    @Schema(description = "处理状态")
    private String status;

    @Schema(description = "处理人ID")
    private Long reviewerId;

    @Schema(description = "处理备注")
    private String reviewNote;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "举报时间")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "处理时间")
    private LocalDateTime updateTime;
}