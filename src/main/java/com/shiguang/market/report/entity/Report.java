package com.shiguang.market.report.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * 举报实体类
 *
 * @author gugu
 */

@Data
@TableName("report")
@Schema(description = "举报实体类")
public class Report {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @Schema(description = "举报ID")
    private Long id;

    @Schema(description = "举报人ID")
    private Long reporterId;

    @Schema(description = "举报类型：ITEM/LOST_FOUND/USER")
    private String targetType;

    @Schema(description = "被举报对象ID")
    private Long targetId;

    @Schema(description = "举报原因")
    private String reason;

    @Schema(description = "风险等级：LOW/MEDIUM/HIGH")
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