package com.shiguang.market.lostfound.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shiguang.market.lostfound.entity.LostFound;
import org.apache.ibatis.annotations.Mapper;

/**
 * 失物招领Mapper接口
 *
 * @author gugu
 */
@Mapper
public interface LostFoundMapper extends BaseMapper<LostFound> {
}
