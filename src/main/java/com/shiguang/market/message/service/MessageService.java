package com.shiguang.market.message.service;

import com.shiguang.market.message.dto.ConversationResponse;
import com.shiguang.market.message.dto.MessageResponse;
import com.shiguang.market.message.dto.SendMessageRequest;

import java.util.List;

/**
 * 消息服务接口
 *
 * @author gugu
 */
public interface MessageService {

    /**
     * 发送消息（自动创建/更新会话）
     */
    MessageResponse sendMessage(Long userId, SendMessageRequest request);

    /**
     * 获取会话列表
     */
    List<ConversationResponse> getConversationList(Long userId);

    /**
     * 获取会话内的消息列表
     */
    List<MessageResponse> getMessageList(Long userId, Long conversationId);

    /**
     * 标记会话已读
     */
    void readMessages(Long userId, Long conversationId);
}