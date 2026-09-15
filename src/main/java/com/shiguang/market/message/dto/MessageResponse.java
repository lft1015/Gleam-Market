package com.shiguang.market.message.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 消息响应 DTO
 *
 * @author gugu
 */
@Data
@Schema(description = "消息响应类")
public class MessageResponse {

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

    @Schema(description = "消息类型")
    private String type;

    @Schema(description = "已读标志")
    private Boolean isRead;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "发送时间")
    private LocalDateTime createTime;
}