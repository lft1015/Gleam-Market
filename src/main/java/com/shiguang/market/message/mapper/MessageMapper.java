package com.shiguang.market.message.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shiguang.market.message.entity.Message;
import org.apache.ibatis.annotations.Mapper;

/**
 * 消息映射器
 *
 * @author gugu
 */

@Mapper
public interface MessageMapper extends BaseMapper<Message> {
}
