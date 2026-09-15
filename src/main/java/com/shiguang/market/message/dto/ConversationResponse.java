package com.shiguang.market.message.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 会话列表响应类
 *
 * @author gugu
 */
@Data
@Schema(description = "会话列表响应类")
public class ConversationResponse {

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

    @Schema(description = "对方用户ID")
    private Long otherUserId;

    @Schema(description = "对方昵称")
    private String otherNickname;

    @Schema(description = "对方头像")
    private String otherAvatar;
}