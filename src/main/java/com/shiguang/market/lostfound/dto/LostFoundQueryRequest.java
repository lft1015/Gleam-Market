package com.shiguang.market.lostfound.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 失物招领查询请求类
 *
 * @author gugu
 */
@Data
@Schema(description = "失物招领查询请求类")
public class LostFoundQueryRequest {

    @Schema(description = "页码", example = "1")
    private Integer page = 1;

    @Schema(description = "每页大小", example = "10")
    private Integer size = 10;

    @Schema(description = "类型：LOST寻物/FOUND招领")
    private String type;

    @Schema(description = "关键字")
    private String keyword;

    @Schema(description = "地点")
    private String location;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "结束时间")
    private LocalDateTime endTime;
}