package com.shiguang.market.message.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shiguang.market.message.entity.Conversation;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会话映射器
 *
 * @author gugu
 */

@Mapper
public interface ConversationMapper extends BaseMapper<Conversation> {
}
