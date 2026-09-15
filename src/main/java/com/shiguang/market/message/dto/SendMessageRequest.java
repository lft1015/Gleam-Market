package com.shiguang.market.message.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 发送消息 DTO
 *
 * @author gugu
 */
@Data
@Schema(description = "发送消息请求类")
public class SendMessageRequest {

    @NotNull(message = "商品ID不能为空")
    @Schema(description = "关联商品ID")
    private Long itemId;

    @NotNull(message = "接收者ID不能为空")
    @Schema(description = "接收者ID")
    private Long receiverId;

    @NotBlank(message = "消息内容不能为空")
    @Schema(description = "消息内容")
    private String content;
}