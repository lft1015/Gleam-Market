package com.shiguang.market.lostfound.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

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
}
