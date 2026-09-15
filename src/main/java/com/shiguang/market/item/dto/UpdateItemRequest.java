package com.shiguang.market.item.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 编辑商品请求类
 *
 * @author gugu
 */
@Data
@Schema(description = "编辑商品请求类")
public class UpdateItemRequest {

    @Schema(description = "商品标题")
    private String title;

    @Schema(description = "商品描述")
    private String description;

    @Schema(description = "商品价格")
    private BigDecimal price;

    @Schema(description = "商品原价")
    private BigDecimal originalPrice;

    @Schema(description = "商品分类")
    private String category;

    @Schema(description = "商品图片")
    private String images;
}