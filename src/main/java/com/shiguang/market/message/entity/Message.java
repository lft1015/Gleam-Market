package com.shiguang.market.message.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * 消息实体类
 *
 * @author gugu
 */
@Data
@TableName("message")
@Schema(description = "消息实体类")
public class Message {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @Schema(description = "消息ID")
    private Long id;

    @Schema(description = "会话ID")
    private Long conversationId;

    @Schema(description = "发送者ID")
    private Long senderId;

    @Schema(description = "接收者ID")
    private Long receiverId;

    @Schema(description = "消息内容")
    private String content;

    @Schema(description = "消息类型：TEXT/IMAGE")
    private String type;

    @Schema(description = "已读标志")
    private Boolean isRead;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "发送时间")
    private LocalDateTime createTime;
}