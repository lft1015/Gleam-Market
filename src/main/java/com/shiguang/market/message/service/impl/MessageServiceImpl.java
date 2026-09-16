package com.shiguang.market.message.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shiguang.market.common.BusinessException;
import com.shiguang.market.message.dto.ConversationResponse;
import com.shiguang.market.message.dto.MessageResponse;
import com.shiguang.market.message.dto.SendMessageRequest;
import com.shiguang.market.message.entity.Conversation;
import com.shiguang.market.message.entity.Message;
import com.shiguang.market.message.mapper.ConversationMapper;
import com.shiguang.market.message.mapper.MessageMapper;
import com.shiguang.market.message.service.MessageService;
import com.shiguang.market.user.entity.User;
import com.shiguang.market.user.mapper.UserMapper;
import com.shiguang.market.item.entity.Item;
import com.shiguang.market.item.mapper.ItemMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 消息服务实现类
 *
 * @author gugu
 */
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageMapper messageMapper;
    private final ConversationMapper conversationMapper;
    private final UserMapper userMapper;
    private final ItemMapper itemMapper;

    /**
     * 发送消息
     * 核心逻辑：找会话 → 有则更新、无则新建 → 插消息
     */
    @Override
    @Transactional
    public MessageResponse sendMessage(Long userId, SendMessageRequest request) {
        if (userId.equals(request.getReceiverId())) {
            throw new BusinessException(400, "不能给自己发消息");
        }
        User receiver = userMapper.selectById(request.getReceiverId());
        Item item = itemMapper.selectById(request.getItemId());
        if (receiver == null) throw new BusinessException(404, "接收者不存在");
        if (item == null) throw new BusinessException(404, "关联商品不存在");

        Long user1 = userId < request.getReceiverId() ? userId : request.getReceiverId();
        Long user2 = userId < request.getReceiverId() ? request.getReceiverId() : userId;

        Conversation conversation = conversationMapper.selectOne(
                new LambdaQueryWrapper<Conversation>()
                        .eq(Conversation::getItemId, request.getItemId())
                        .eq(Conversation::getUser1Id, user1)
                        .eq(Conversation::getUser2Id, user2)
        );

        if (conversation == null) {
            conversation = new Conversation();
            conversation.setItemId(request.getItemId());
            conversation.setUser1Id(user1);
            conversation.setUser2Id(user2);
            conversation.setUnreadCount(0);
            conversation.setCreateTime(LocalDateTime.now());
            conversation.setUpdateTime(LocalDateTime.now());
            conversationMapper.insert(conversation);
        }

        conversation.setLastMessage(request.getContent());
        conversation.setLastTime(LocalDateTime.now());
        conversation.setUnreadCount(conversation.getUnreadCount() + 1);
        conversation.setUpdateTime(LocalDateTime.now());
        conversationMapper.updateById(conversation);

        Message message = new Message();
        message.setConversationId(conversation.getId());
        message.setSenderId(userId);
        message.setReceiverId(request.getReceiverId());
        message.setContent(request.getContent());
        message.setType("TEXT");
        message.setIsRead(false);
        message.setCreateTime(LocalDateTime.now());
        messageMapper.insert(message);

        MessageResponse response = new MessageResponse();
        BeanUtil.copyProperties(message, response);
        return response;
    }

    /**
     * 获取会话列表
     * 查我是参与者的所有会话，按最后消息时间倒序
     */
    @Override
    public List<ConversationResponse> getConversationList(Long userId) {
        List<Conversation> conversations = conversationMapper.selectList(
                new LambdaQueryWrapper<Conversation>()
                        .and(w -> w.eq(Conversation::getUser1Id, userId)
                                .or()
                                .eq(Conversation::getUser2Id, userId))
                        .orderByDesc(Conversation::getLastTime)
        );
        return conversations.stream().map(c -> {
            ConversationResponse response = new ConversationResponse();
            BeanUtil.copyProperties(c, response);
            Long otherId = userId.equals(c.getUser1Id()) ? c.getUser2Id() : c.getUser1Id();
            User other = userMapper.selectById(otherId);
            if (other != null) {
                response.setOtherUserId(other.getId());
                response.setOtherNickname(other.getNickname());
                response.setOtherAvatar(other.getAvatar());
            }
            Long unread = messageMapper.selectCount(new LambdaQueryWrapper<Message>()
                    .eq(Message::getConversationId, c.getId())
                    .eq(Message::getReceiverId, userId)
                    .eq(Message::getIsRead, false));
            response.setUnreadCount(unread.intValue());
            return response;
        }).toList();
    }

    /**
     * 获取会话内的消息列表
     * 按发送时间正序，带权限校验
     */
    @Override
    public List<MessageResponse> getMessageList(Long userId, Long conversationId) {
        Conversation conversation = conversationMapper.selectById(conversationId);
        if (conversation == null) {
            throw new BusinessException(404, "会话不存在");
        }
        if (!userId.equals(conversation.getUser1Id()) && !userId.equals(conversation.getUser2Id())) {
            throw new BusinessException(403, "无权查看该会话");
        }

        List<Message> messages = messageMapper.selectList(
                new LambdaQueryWrapper<Message>()
                        .eq(Message::getConversationId, conversationId)
                        .orderByAsc(Message::getCreateTime)
        );
        return messages.stream().map(m -> {
            MessageResponse response = new MessageResponse();
            BeanUtil.copyProperties(m, response);
            return response;
        }).toList();
    }

    /**
     * 标记会话已读
     * 将未读数清零 + 该会话内所有发给当前用户的消息标为已读
     */
    @Override
    @Transactional
    public void readMessages(Long userId, Long conversationId) {
        Conversation conversation = conversationMapper.selectById(conversationId);
        if (conversation == null) {
            throw new BusinessException(404, "会话不存在");
        }
        if (!userId.equals(conversation.getUser1Id()) && !userId.equals(conversation.getUser2Id())) {
            throw new BusinessException(403, "无权操作该会话");
        }

        conversation.setUnreadCount(0);
        conversation.setUpdateTime(LocalDateTime.now());
        conversationMapper.updateById(conversation);

        Message update = new Message();
        update.setIsRead(true);
        messageMapper.update(update,
                new LambdaQueryWrapper<Message>()
                        .eq(Message::getConversationId, conversationId)
                        .eq(Message::getReceiverId, userId)
                        .eq(Message::getIsRead, false)
        );
    }
}
