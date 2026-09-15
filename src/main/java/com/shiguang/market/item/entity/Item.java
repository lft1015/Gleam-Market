package com.shiguang.market.item.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 二手商品实体类
 *
 * @author gugu
 */

@Data
@TableName("item")
@Schema(description = "二手商品实体类")
public class Item {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @Schema(description = "商品ID")
    private Long id;

    @Schema(description = "商品用户ID")
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

    @Schema(description = "商品图片列表")
    private String images;

    @Schema(description = "商品浏览次数")
    private Integer viewCount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "商品创建时间")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "商品更新时间")
    private LocalDateTime updateTime;

    @TableLogic(value = "0", delval = "1")
    @Schema(description = "商品逻辑删除")
    private Boolean deleted;
}
