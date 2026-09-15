package com.shiguang.market.review.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.shiguang.market.common.Result;
import com.shiguang.market.review.dto.ReviewDecisionRequest;
import com.shiguang.market.review.dto.ReviewResponse;
import com.shiguang.market.review.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping
    public Result<IPage<ReviewResponse>> list(@RequestParam(required = false) String status,
                                              @RequestParam(defaultValue = "1") Integer page,
                                              @RequestParam(defaultValue = "10") Integer size) {
        return Result.ok(reviewService.pageQuery(status, page, size));
    }

    @PostMapping("/{reviewId}/decision")
    public Result<Void> decide(@PathVariable Long reviewId,
                               @Valid @RequestBody ReviewDecisionRequest request) {
        Long reviewerId = (Long) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        reviewService.decide(reviewerId, reviewId, request);
        return Result.ok();
    }
}