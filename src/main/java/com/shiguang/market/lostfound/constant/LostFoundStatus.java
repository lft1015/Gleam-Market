package com.shiguang.market.lostfound.constant;

/**
 * 失物招领状态常量
 *
 * @author gugu
 */
public final class LostFoundStatus {

    private LostFoundStatus() {}

    /** 待处理 */
    public static final String PENDING = "PENDING";

    /** 处理中 */
    public static final String PROCESSING = "PROCESSING";

    /** 已找回 */
    public static final String FOUND = "FOUND";

    /** 已归还 */
    public static final String RETURNED = "RETURNED";

    /** 已关闭 */
    public static final String CLOSED = "CLOSED";
}
