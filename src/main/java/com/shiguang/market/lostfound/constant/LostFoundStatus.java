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
     * 处理中
     */
    public static final String IN_PROGRESS = "IN_PROGRESS";
    /**
     * 已拒绝
     */
    public static final String REJECTED = "REJECTED";
    /**
     * 已解决
     */
    public static final String RESOLVED = "RESOLVED";
    /**
     * 已关闭
     */
    public static final String CLOSED = "CLOSED";

    private LostFoundStatus() {}
}