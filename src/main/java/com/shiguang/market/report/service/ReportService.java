package com.shiguang.market.report.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.shiguang.market.report.dto.HandleReportRequest;
import com.shiguang.market.report.dto.ReportResponse;
import com.shiguang.market.report.dto.SubmitReportRequest;

/**
 * 举报服务接口
 *
 * @author gugu
 */
public interface ReportService {

    /**
     * 提交举报
     */
    void submit(Long reporterId, SubmitReportRequest request);

    /**
     * 分页查询举报列表（管理员）
     */
    IPage<ReportResponse> pageQuery(String status, Integer page, Integer size);

    /**
     * 处理举报（管理员）
     */
    void handle(Long reviewerId, Long reportId, HandleReportRequest request);

    /**
     * 分页查询我的举报（用户）
     */
    IPage<ReportResponse> getMyReports(Long userId, Integer page, Integer size);
}