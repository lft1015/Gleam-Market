package com.shiguang.market.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * 管理员操作日志实体
 *
 * @author gugu
 */
@Data
@TableName("audit_log")
@Schema(description = "管理员操作日志实体")
public class AuditLog {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @Schema(description = "日志ID")
    private Long id;

    @Schema(description = "操作管理员ID")
    private Long adminId;

    @Schema(description = "操作类型")
    private String action;

    @Schema(description = "操作对象类型")
    private String targetType;

    @Schema(description = "操作对象ID")
    private Long targetId;

    @Schema(description = "操作详情")
    private String detail;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "操作时间")
    private LocalDateTime createTime;
}