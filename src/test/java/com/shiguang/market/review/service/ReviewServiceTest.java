package com.shiguang.market.review.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.shiguang.market.BaseTest;
import com.shiguang.market.common.BusinessException;
import com.shiguang.market.item.constant.ItemStatus;
import com.shiguang.market.item.entity.Item;
import com.shiguang.market.item.mapper.ItemMapper;
import com.shiguang.market.lostfound.constant.LostFoundStatus;
import com.shiguang.market.lostfound.entity.LostFound;
import com.shiguang.market.lostfound.mapper.LostFoundMapper;
import com.shiguang.market.review.dto.ReviewDecisionRequest;
import com.shiguang.market.review.dto.ReviewResponse;
import com.shiguang.market.review.entity.Review;
import com.shiguang.market.review.mapper.ReviewMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("审核服务集成测试")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class ReviewServiceTest extends BaseTest {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ReviewMapper reviewMapper;

    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private LostFoundMapper lostFoundMapper;

    @Test
    @DisplayName("创建审核记录成功")
    void createReview_success() {
        reviewService.createReview(1L, "ITEM", 1L);

        Review review = reviewMapper.selectOne(
                new LambdaQueryWrapper<Review>()
                        .eq(Review::getTargetType, "ITEM")
                        .eq(Review::getTargetId, 1L)
                        .orderByDesc(Review::getCreateTime)
                        .last("LIMIT 1"));
        assertNotNull(review);
        assertEquals("PENDING", review.getStatus());
        assertEquals(1L, review.getSubmitterId());
    }

    @Test
    @DisplayName("分页查询审核列表 - 全部")
    void pageQuery_all() {
        IPage<ReviewResponse> page = reviewService.pageQuery(null, 1, 10);
        assertTrue(page.getTotal() >= 1);
    }

    @Test
    @DisplayName("分页查询审核列表 - 按状态")
    void pageQuery_byStatus() {
        IPage<ReviewResponse> page = reviewService.pageQuery("PENDING", 1, 10);
        page.getRecords().forEach(r -> assertEquals("PENDING", r.getStatus()));
    }

    @Test
    @DisplayName("审核通过 - 商品")
    void decide_approveItem() {
        Review review = reviewMapper.selectOne(
                new LambdaQueryWrapper<Review>().eq(Review::getStatus, "PENDING").last("LIMIT 1"));
        assertNotNull(review, "需要PENDING状态的审核记录");

        ReviewDecisionRequest req = new ReviewDecisionRequest();
        req.setStatus("APPROVED");
        req.setReviewNote("审核通过");

        String targetType = review.getTargetType();
        reviewService.decide(2L, review.getId(), req);

        Review updated = reviewMapper.selectById(review.getId());
        assertEquals("APPROVED", updated.getStatus());
        assertEquals(2L, updated.getReviewerId());

        if ("ITEM".equals(targetType)) {
            Item item = itemMapper.selectById(review.getTargetId());
            assertEquals(ItemStatus.ON_SALE, item.getStatus());
        }
    }

    @Test
    @DisplayName("审核拒绝 - 商品")
    void decide_rejectItem() {
        reviewService.createReview(3L, "ITEM", 2L);
        Review review = reviewMapper.selectOne(
                new LambdaQueryWrapper<Review>()
                        .eq(Review::getTargetType, "ITEM")
                        .eq(Review::getTargetId, 2L)
                        .eq(Review::getStatus, "PENDING")
                        .last("LIMIT 1"));

        ReviewDecisionRequest req = new ReviewDecisionRequest();
        req.setStatus("REJECTED");
        req.setReviewNote("违规内容");

        reviewService.decide(2L, review.getId(), req);

        Review updated = reviewMapper.selectById(review.getId());
        assertEquals("REJECTED", updated.getStatus());

        Item item = itemMapper.selectById(2L);
        assertEquals(ItemStatus.REJECTED, item.getStatus());
    }

    @Test
    @DisplayName("审核失败 - 记录不存在")
    void decide_notFound() {
        ReviewDecisionRequest req = new ReviewDecisionRequest();
        req.setStatus("APPROVED");

        BusinessException ex = assertThrows(BusinessException.class,
                () -> reviewService.decide(2L, 9999L, req));
        assertEquals(404, ex.getCode());
    }

    @Test
    @DisplayName("审核失败 - 已处理")
    void decide_alreadyProcessed() {
        Review review = reviewMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Review>()
                        .eq(Review::getStatus, "APPROVED").last("LIMIT 1"));
        assertNotNull(review, "需要已处理的审核记录");

        ReviewDecisionRequest req = new ReviewDecisionRequest();
        req.setStatus("REJECTED");

        BusinessException ex = assertThrows(BusinessException.class,
                () -> reviewService.decide(2L, review.getId(), req));
        assertEquals(400, ex.getCode());
    }
}