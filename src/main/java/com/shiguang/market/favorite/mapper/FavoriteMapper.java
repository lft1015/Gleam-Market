package com.shiguang.market.favorite.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shiguang.market.favorite.entity.Favorite;
import org.apache.ibatis.annotations.Mapper;

/**
 * 收藏 数据访问层
 *
 * @author gugu
 */
@Mapper
public interface FavoriteMapper extends BaseMapper<Favorite> {
}