package com.shiguang.market.message.controller;

import com.shiguang.market.common.Result;
import com.shiguang.market.message.dto.ConversationResponse;
import com.shiguang.market.message.dto.MessageResponse;
import com.shiguang.market.message.dto.SendMessageRequest;
import com.shiguang.market.message.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 消息控制器
 *
 * @author gugu
 */
@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    /**
     * 发送消息
     */
    @PostMapping
    public Result<MessageResponse> send(@Valid @RequestBody SendMessageRequest request) {
        Long userId = (Long) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return Result.ok(messageService.sendMessage(userId, request));
    }

    /**
     * 会话内的消息列表
     */
    @GetMapping("/conversations/{conversationId}")
    public Result<List<MessageResponse>> messages(@PathVariable Long conversationId) {
        Long userId = (Long) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return Result.ok(messageService.getMessageList(userId, conversationId));
    }

    /**
     * 标记会话已读
     */
    @PutMapping("/conversations/{conversationId}/read")
    public Result<Void> read(@PathVariable Long conversationId) {
        Long userId = (Long) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        messageService.readMessages(userId, conversationId);
        return Result.ok();
    }

    /**
     * 会话列表
     */
    @GetMapping("/conversations")
    public Result<List<ConversationResponse>> conversations() {
        Long userId = (Long) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return Result.ok(messageService.getConversationList(userId));
    }
}