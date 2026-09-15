package com.shiguang.market.message.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * 会话实体类
 *
 * @author gugu
 */
@Data
@TableName("conversation")
@Schema(description = "会话实体类")
public class Conversation {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @Schema(description = "会话ID")
    private Long id;

    @Schema(description = "关联商品ID")
    private Long itemId;

    @Schema(description = "参与者A")
    private Long user1Id;

    @Schema(description = "参与者B")
    private Long user2Id;

    @Schema(description = "最后一条消息")
    private String lastMessage;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "最后消息时间")
    private LocalDateTime lastTime;

    @Schema(description = "未读数")
    private Integer unreadCount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @TableLogic(value = "0", delval = "1")
    private Boolean deleted;
}