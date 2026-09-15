package com.shiguang.market.lostfound.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * 失物招领实体类
 */

@Data
@TableName("lost_found")
@Schema(description = "失物招领实体类")
public class LostFound {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @Schema(description = "失物招领ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "失物招领标题")
    private String title;

    @Schema(description = "失物招领描述")
    private String description;

    @Schema(description = "失物招领类型")
    private String type;

    @Schema(description = "失物招领位置")
    private String location;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "失物招领时间")
    private LocalDateTime lostTime;

    @Schema(description = "失物招领联系人")
    private String contact;

    @Schema(description = "失物招领图片")
    private String images;

    @Schema(description = "失物招领状态")
    private String status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "失物招领创建时间")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "失物招领更新时间")
    private LocalDateTime updateTime;

    @TableLogic(value = "0", delval = "1")
    private Boolean deleted;
}
