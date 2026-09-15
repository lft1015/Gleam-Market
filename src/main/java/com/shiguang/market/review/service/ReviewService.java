package com.shiguang.market.review.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.shiguang.market.review.dto.ReviewDecisionRequest;
import com.shiguang.market.review.dto.ReviewResponse;

/**
 * 审核服务接口
 *
 * @author gugu
 */
public interface ReviewService {

    /**
     * 创建审核记录（商品/失物发布时调用）
     */
    void createReview(Long submitterId, String targetType, Long targetId);

    /**
     * 分页查询审核列表（管理员）
     */
    IPage<ReviewResponse> pageQuery(String status, Integer page, Integer size);

    /**
     * 处理审核决定（通过/拒绝），同步更新商品/失物状态
     */
    void decide(Long reviewerId, Long reviewId, ReviewDecisionRequest request);
}