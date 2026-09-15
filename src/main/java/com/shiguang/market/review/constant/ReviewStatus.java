package com.shiguang.market.review.constant;

import java.io.Serial;

/**
 * 审核状态
 *
 * @author gugu
 */
public class ReviewStatus {
    /**
     * 待审核
     */
    public static final String PENDING = "PENDING";
    /**
     * 已通过
     */
    public static final String APPROVED = "APPROVED";
    /**
     * 已拒绝
     */
    public static final String REJECTED = "REJECTED";

    private ReviewStatus() {}
}