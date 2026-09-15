package com.shiguang.market.item.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品响应
 *
 * @author gugu
 */

@Data
public class ItemResponse {
    // 商品ID
    private Long id;

    //商品用户ID
    private Long userId;

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

    //商品状态
    private String status;

    // 商品图片
    private String images;

    //商品浏览量
    private Integer viewCount;

    // 商品创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    // 商品更新时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
