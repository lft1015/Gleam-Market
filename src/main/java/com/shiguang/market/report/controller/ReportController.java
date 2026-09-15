package com.shiguang.market.report.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.shiguang.market.common.Result;
import com.shiguang.market.report.dto.HandleReportRequest;
import com.shiguang.market.report.dto.ReportResponse;
import com.shiguang.market.report.dto.SubmitReportRequest;
import com.shiguang.market.report.service.ReportService;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * 举报控制器
 *
 * @author gugu
 */

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
@Schema(description = "举报控制器")
public class ReportController {

    private final ReportService reportService;

    /**
     * 提交举报
     */
    @PostMapping
    public Result<Void> submit(@Valid @RequestBody SubmitReportRequest request) {
        Long userId = (Long) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        reportService.submit(userId, request);
        return Result.ok();
    }

    /**
     * 分页查询我的举报（用户）
     */
    @GetMapping("/my")
    public Result<IPage<ReportResponse>> myReports(@RequestParam(defaultValue = "1") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        Long userId = (Long) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return Result.ok(reportService.getMyReports(userId, page, size));
    }

    /**
     * 分页查询举报列表（管理员）
     */
    @GetMapping("/admin")
    public Result<IPage<ReportResponse>> adminList(@RequestParam(required = false) String status,
                                                   @RequestParam(defaultValue = "1") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        return Result.ok(reportService.pageQuery(status, page, size));
    }

    /**
     * 处理举报（管理员）
     */
    @PutMapping("/admin/{reportId}")
    public Result<Void> handle(@PathVariable Long reportId,
                               @Valid @RequestBody HandleReportRequest request) {
        Long reviewerId = (Long) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        reportService.handle(reviewerId, reportId, request);
        return Result.ok();
    }
}