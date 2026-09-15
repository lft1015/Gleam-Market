package com.shiguang.market.review.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.time.LocalDateTime;
/**
 * 审核记录
 *
 * @author gugu
 */
@Data
@TableName("review")
@Schema(description = "审核实体类")
public class Review {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @Schema(description = "审核ID")
    private Long id;

    @Schema(description = "审核类型：ITEM/LOST_FOUND")
    private String targetType;

    @Schema(description = "被审核对象ID")
    private Long targetId;

    @Schema(description = "提交人ID")
    private Long submitterId;

    @Schema(description = "审核人ID")
    private Long reviewerId;

    @Schema(description = "审核状态")
    private String status;

    @Schema(description = "审核备注")
    private String reviewNote;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "提交时间")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "审核时间")
    private LocalDateTime updateTime;
}