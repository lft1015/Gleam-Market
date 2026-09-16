package com.shiguang.market.lostfound.constant;

/**
 * 失物招领状态常量
 *
 * @author gugu
 */
public class LostFoundStatus {
    /**
     * 待处理
     */
    public static final String PENDING_REVIEW = "PENDING_REVIEW";
    /**
     * 暂停
     */
    public static final String PENDING = "PENDING";

    /**
     * 审核拒绝
     */
    public static final String REJECTED = "REJECTED";

    /**
     * 进行中
     */
    public static final String IN_PROGRESS = "IN_PROGRESS";

    /**
     * 处理中
     */
    public static final String PROCESSING = "PROCESSING";

    /**
     * 已找回
     */
    public static final String FOUND = "FOUND";

    /**
     * 已归还
     */
    public static final String RETURNED = "RETURNED";

    /**
     * 已关闭
     */
    public static final String CLOSED = "CLOSED";

    private LostFoundStatus() {}
}