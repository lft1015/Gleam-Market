package com.shiguang.market.message.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shiguang.market.BaseTest;
import com.shiguang.market.message.dto.MessageResponse;
import com.shiguang.market.message.dto.SendMessageRequest;
import com.shiguang.market.message.entity.Conversation;
import com.shiguang.market.message.entity.Message;
import com.shiguang.market.message.mapper.ConversationMapper;
import com.shiguang.market.message.mapper.MessageMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("消息服务集成测试")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class MessageServiceTest extends BaseTest {

    @Autowired
    private MessageService messageService;

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private ConversationMapper conversationMapper;

    @Test
    @DisplayName("发送消息成功 - 新建会话")
    void sendMessage_newConversation() {
        SendMessageRequest req = new SendMessageRequest();
        req.setItemId(1L);
        req.setReceiverId(3L);
        req.setContent("test message hello");

        MessageResponse resp = messageService.sendMessage(1L, req);

        assertNotNull(resp);
        assertNotNull(resp.getConversationId());
        assertEquals("test message hello", resp.getContent());

        List<Message> messages = messageMapper.selectList(
                new LambdaQueryWrapper<Message>()
                        .eq(Message::getConversationId, resp.getConversationId()));
        assertEquals(1, messages.size());
    }

    @Test
    @DisplayName("发送消息成功 - 已有会话追加")
    void sendMessage_existingConversation() {
        SendMessageRequest req1 = new SendMessageRequest();
        req1.setItemId(1L);
        req1.setReceiverId(3L);
        req1.setContent("first message");
        MessageResponse resp1 = messageService.sendMessage(1L, req1);
        Long conversationId = resp1.getConversationId();

        SendMessageRequest req2 = new SendMessageRequest();
        req2.setItemId(1L);
        req2.setReceiverId(3L);
        req2.setContent("second message");
        MessageResponse resp2 = messageService.sendMessage(1L, req2);

        assertEquals(conversationId, resp2.getConversationId());

        List<Message> messages = messageMapper.selectList(
                new LambdaQueryWrapper<Message>()
                        .eq(Message::getConversationId, conversationId));
        assertEquals(2, messages.size());
    }
}