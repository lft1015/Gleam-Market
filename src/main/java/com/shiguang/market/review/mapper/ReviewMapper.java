package com.shiguang.market.review.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shiguang.market.review.entity.Review;
import org.apache.ibatis.annotations.Mapper;

/**
 * 审核映射器
 *
 * @author gugu
 */
@Mapper
public interface ReviewMapper extends BaseMapper<Review> {
}