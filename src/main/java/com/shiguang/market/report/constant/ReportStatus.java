package com.shiguang.market.report.constant;

/**
 * 举报状态常量类
 *
 * @author gugu
 */
public class ReportStatus {

    /**
     * 待处理
     */
    public static final String PENDING = "PENDING";

    /**
     * 已处理
     */
    public static final String RESOLVED = "RESOLVED";

    /**
     * 已忽略
     */
    public static final String DISMISSED = "DISMISSED";

    /** 风险等级 */
    public static final String RISK_LOW = "LOW";
    public static final String RISK_MEDIUM = "MEDIUM";
    public static final String RISK_HIGH = "HIGH";

    private ReportStatus() {}
}