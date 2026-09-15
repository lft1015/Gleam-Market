package com.shiguang.market.item.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 商品查询请求类
 *
 * @author gugu
 */
@Data
@Schema(description = "商品查询请求类")
public class ItemQueryRequest {

    @Schema(description = "页码", example = "1")
    private Integer page = 1;

    @Schema(description = "每页大小", example = "10")
    private Integer size = 10;

    @Schema(description = "商品分类")
    private String category;

    @Schema(description = "商品状态")
    private String status;
}
