package com.shiguang.market.claim.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.time.LocalDateTime;

@Data
@TableName("claim")
@Schema(description = "失物认领申请实体")
public class Claim {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @Schema(description = "认领ID")
    private Long id;

    @Schema(description = "失物招领ID")
    private Long lostFoundId;

    @Schema(description = "认领人ID")
    private Long claimantId;

    @Schema(description = "认领说明")
    private String message;

    @Schema(description = "联系方式")
    private String contact;

    @Schema(description = "凭证信息")
    private String verification;

    @Schema(description = "认领状态")
    private String status;

    @Schema(description = "审核人ID")
    private Long reviewerId;

    @Schema(description = "审核备注")
    private String reviewNote;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "申请时间")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "处理时间")
    private LocalDateTime updateTime;
}