package com.shiguang.market.item.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 发布商品请求
 *
 * @author gugu
 */

@Data
public class PublishItemRequest {

    // 商品标题
    private String title;
    // 商品描述
    private String description;
    // 商品价格
    private BigDecimal price;
    // 商品原价
    private BigDecimal originalPrice;
    // 商品分类
    private String category;
    // 商品图片
    private String images;
}
