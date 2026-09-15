package com.shiguang.market.lostfound.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 失物招领响应类
 */

@Data
@Schema(description = "失物招领响应类")
public class LostFoundResponse {

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

    @Schema(description = "失物联系电话")
    private String contact;

    @Schema(description = "失物招领图片")
    private String images;

    @Schema(description = "失物招领状态")
    private String status;

    @Schema(description = "失物时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lostTime;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

}
