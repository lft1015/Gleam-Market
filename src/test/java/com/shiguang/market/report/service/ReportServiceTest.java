package com.shiguang.market.report.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.shiguang.market.BaseTest;
import com.shiguang.market.common.BusinessException;
import com.shiguang.market.report.dto.HandleReportRequest;
import com.shiguang.market.report.dto.ReportResponse;
import com.shiguang.market.report.dto.SubmitReportRequest;
import com.shiguang.market.report.entity.Report;
import com.shiguang.market.report.mapper.ReportMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("举报服务集成测试")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class ReportServiceTest extends BaseTest {

    @Autowired
    private ReportService reportService;

    @Autowired
    private ReportMapper reportMapper;

    @Test
    @DisplayName("提交举报成功")
    void submit_success() {
        SubmitReportRequest req = new SubmitReportRequest();
        req.setTargetType("ITEM");
        req.setTargetId(2L);
        req.setReason("test report reason");

        reportService.submit(1L, req);

        Report r = reportMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Report>()
                        .eq(Report::getTargetType, "ITEM")
                        .eq(Report::getTargetId, 2L)
                        .eq(Report::getReporterId, 1L)
                        .eq(Report::getReason, "test report reason"));
        assertNotNull(r);
        assertEquals("PENDING", r.getStatus());
    }

    @Test
    @DisplayName("处理举报成功 - 解决")
    void handle_success() {
        SubmitReportRequest req = new SubmitReportRequest();
        req.setTargetType("USER");
        req.setTargetId(3L);
        req.setReason("handle test report");
        reportService.submit(1L, req);

        Report r = reportMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Report>()
                        .eq(Report::getTargetType, "USER")
                        .eq(Report::getTargetId, 3L)
                        .eq(Report::getReporterId, 1L)
                        .eq(Report::getReason, "handle test report"));

        HandleReportRequest handle = new HandleReportRequest();
        handle.setStatus("RESOLVED");
        handle.setReviewNote("handled ok");

        reportService.handle(2L, r.getId(), handle);

        Report updated = reportMapper.selectById(r.getId());
        assertEquals("RESOLVED", updated.getStatus());
        assertEquals(2L, updated.getReviewerId());
    }

    @Test
    @DisplayName("处理举报失败 - 已处理")
    void handle_alreadyProcessed() {
        SubmitReportRequest req = new SubmitReportRequest();
        req.setTargetType("ITEM");
        req.setTargetId(2L);
        req.setReason("already processed test");
        reportService.submit(1L, req);

        Report r = reportMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Report>()
                        .eq(Report::getTargetType, "ITEM")
                        .eq(Report::getTargetId, 2L)
                        .eq(Report::getReporterId, 1L)
                        .eq(Report::getReason, "already processed test"));

        HandleReportRequest handle = new HandleReportRequest();
        handle.setStatus("DISMISSED");
        reportService.handle(2L, r.getId(), handle);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> reportService.handle(2L, r.getId(), handle));
        assertEquals(400, ex.getCode());
    }

    @Test
    @DisplayName("分页查询举报列表")
    void pageQuery_success() {
        IPage<ReportResponse> page = reportService.pageQuery("PENDING", 1, 10);
        assertNotNull(page);
        assertTrue(page.getTotal() >= 1);
    }

    @Test
    @DisplayName("分页查询我的举报")
    void getMyReports_success() {
        IPage<ReportResponse> page = reportService.getMyReports(3L, 1, 10);
        assertNotNull(page);
        assertTrue(page.getTotal() >= 1);
    }
}