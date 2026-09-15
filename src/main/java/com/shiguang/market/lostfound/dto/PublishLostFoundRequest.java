package com.shiguang.market.lostfound.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 发布失物招领请求类
 */
@Data
@Schema(description = "发布失物招领请求类")
public class PublishLostFoundRequest {

    @NotBlank(message = "标题不能为空")
    @Schema(description = "失物招领标题")
    private String title;

    @Schema(description = "失物招领描述")
    private String description;

    @Schema(description = "失物招领图片")
    private String images;

    @NotBlank(message = "类型不能为空")
    @Schema(description = "失物招领类型")
    private String type;

    @NotBlank(message = "位置不能为空")
    @Schema(description = "失物招领位置")
    private String location;

    @NotBlank(message = "联系方式不能为空")
    @Schema(description = "失物招领联系方式")
    private String contact;

    @NotNull(message = "时间不能为空")
    @Schema(description = "丢失/捡到时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lostFoundTime;
}