package com.shiguang.market.review.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shiguang.market.common.BusinessException;
import com.shiguang.market.item.constant.ItemStatus;
import com.shiguang.market.item.entity.Item;
import com.shiguang.market.item.mapper.ItemMapper;
import com.shiguang.market.lostfound.constant.LostFoundStatus;
import com.shiguang.market.lostfound.entity.LostFound;
import com.shiguang.market.lostfound.mapper.LostFoundMapper;
import com.shiguang.market.message.mq.NotificationProducer;
import com.shiguang.market.message.mq.dto.ReviewNotificationMessage;
import com.shiguang.market.review.constant.ReviewStatus;
import com.shiguang.market.review.dto.ReviewDecisionRequest;
import com.shiguang.market.review.dto.ReviewResponse;
import com.shiguang.market.review.entity.Review;
import com.shiguang.market.review.mapper.ReviewMapper;
import com.shiguang.market.review.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 商品审核服务实现类
 *
 * @author gugu
 */
@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewMapper reviewMapper;
    private final ItemMapper itemMapper;
    private final LostFoundMapper lostFoundMapper;
    private final NotificationProducer notificationProducer;

    public ReviewServiceImpl(ReviewMapper reviewMapper,
                             ItemMapper itemMapper,
                             LostFoundMapper lostFoundMapper,
                             @Autowired(required = false) NotificationProducer notificationProducer) {
        this.reviewMapper = reviewMapper;
        this.itemMapper = itemMapper;
        this.lostFoundMapper = lostFoundMapper;
        this.notificationProducer = notificationProducer;
    }

    /**
     * 创建审核记录
     */
    @Override
    public void createReview(Long submitterId, String targetType, Long targetId) {
        Review review = new Review();
        review.setTargetType(targetType);
        review.setTargetId(targetId);
        review.setSubmitterId(submitterId);
        review.setStatus(ReviewStatus.PENDING);
        review.setCreateTime(LocalDateTime.now());
        review.setUpdateTime(LocalDateTime.now());
        reviewMapper.insert(review);
    }

    /**
     * 分页查询审核列表（管理员）
     */
    @Override
    public IPage<ReviewResponse> pageQuery(String status, Integer pageNum, Integer pageSize) {
        Page<Review> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.hasText(status), Review::getStatus, status);
        wrapper.orderByAsc(Review::getStatus)
                .orderByDesc(Review::getCreateTime);

        Page<Review> result = reviewMapper.selectPage(page, wrapper);

        Page<ReviewResponse> responsePage = new Page<>(pageNum, pageSize, result.getTotal());
        responsePage.setRecords(result.getRecords().stream().map(r -> {
            ReviewResponse resp = new ReviewResponse();
            BeanUtil.copyProperties(r, resp);
            return resp;
        }).toList());
        return responsePage;
    }

    /**
     * 处理审核决定（通过/拒绝），同步更新商品/失物状态
     */
    @Override
    public void decide(Long reviewerId, Long reviewId, ReviewDecisionRequest request) {
        Review review = reviewMapper.selectById(reviewId);
        if (review == null) {
            throw new BusinessException(404, "审核记录不存在");
        }
        if (!ReviewStatus.PENDING.equals(review.getStatus())) {
            throw new BusinessException(400, "该记录已审核，不能重复处理");
        }

        String decision = request.getStatus();

        if (ReviewStatus.APPROVED.equals(decision)) {
            approveTarget(review.getTargetType(), review.getTargetId());
        } else if (ReviewStatus.REJECTED.equals(decision)) {
            rejectTarget(review.getTargetType(), review.getTargetId());
        }

        review.setStatus(decision);
        review.setReviewerId(reviewerId);
        review.setReviewNote(request.getReviewNote());
        review.setUpdateTime(LocalDateTime.now());
        reviewMapper.updateById(review);

        // 异步通知提交人审核结果
        if (notificationProducer != null) {
            ReviewNotificationMessage notification = new ReviewNotificationMessage(
                    review.getSubmitterId(),
                    decision,
                    review.getTargetType(),
                    review.getTargetId(),
                    request.getReviewNote());
            notificationProducer.sendReviewNotification(notification);
        }
    }

    /**
     * 审核通过，更新商品/失物状态
     */
    private void approveTarget(String targetType, Long targetId) {
        if ("ITEM".equals(targetType)) {
            Item item = itemMapper.selectById(targetId);
            if (item == null) throw new BusinessException(404, "商品不存在");
            item.setStatus(ItemStatus.ON_SALE);
            item.setUpdateTime(LocalDateTime.now());
            itemMapper.updateById(item);
        } else if ("LOST_FOUND".equals(targetType)) {
            LostFound lostFound = lostFoundMapper.selectById(targetId);
            if (lostFound == null) throw new BusinessException(404, "失物招领不存在");
            lostFound.setStatus(LostFoundStatus.IN_PROGRESS);
            lostFound.setUpdateTime(LocalDateTime.now());
            lostFoundMapper.updateById(lostFound);
        }
    }

    /**
     * 审核拒绝，更新商品/失物状态
     */
    private void rejectTarget(String targetType, Long targetId) {
        if ("ITEM".equals(targetType)) {
            Item item = itemMapper.selectById(targetId);
            if (item == null) throw new BusinessException(404, "商品不存在");
            item.setStatus(ItemStatus.REJECTED);
            item.setUpdateTime(LocalDateTime.now());
            itemMapper.updateById(item);
        } else if ("LOST_FOUND".equals(targetType)) {
            LostFound lostFound = lostFoundMapper.selectById(targetId);
            if (lostFound == null) throw new BusinessException(404, "失物招领不存在");
            lostFound.setStatus(LostFoundStatus.REJECTED);
            lostFound.setUpdateTime(LocalDateTime.now());
            lostFoundMapper.updateById(lostFound);
        }
    }
}