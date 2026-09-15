package com.shiguang.market.item.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 发布商品请求
 *
 * @author gugu
 */

@Data
@Schema(description = "发布商品请求类")
public class PublishItemRequest {

    @NotBlank(message = "标题不能为空")
    @Schema(description = "商品标题")
    private String title;

    @Schema(description = "商品描述")
    private String description;

    @NotNull(message = "价格不能为空")
    @Schema(description = "商品价格")
    private BigDecimal price;

    @Schema(description = "商品原价")
    private BigDecimal originalPrice;

    @NotBlank(message = "分类不能为空")
    @Schema(description = "商品分类")
    private String category;

    @Schema(description = "商品图片")
    private String images;
}