package com.shiguang.market.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shiguang.market.user.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户 Mapper层
 *
 * @author gugu
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
