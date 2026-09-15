package com.shiguang.market.item.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品响应
 *
 * @author gugu
 */

@Data
@Schema(description = "商品响应类")
public class ItemResponse {

    @Schema(description = "商品ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

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

    @Schema(description = "商品状态")
    private String status;

    @Schema(description = "商品图片")
    private String images;

    @Schema(description = "浏览量")
    private Integer viewCount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}