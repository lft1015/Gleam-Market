package com.shiguang.market.report.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shiguang.market.common.BusinessException;
import com.shiguang.market.message.mq.NotificationProducer;
import com.shiguang.market.message.mq.dto.ReportNotificationMessage;
import com.shiguang.market.report.constant.ReportStatus;
import com.shiguang.market.report.dto.HandleReportRequest;
import com.shiguang.market.report.dto.ReportResponse;
import com.shiguang.market.report.dto.SubmitReportRequest;
import com.shiguang.market.report.entity.Report;
import com.shiguang.market.report.mapper.ReportMapper;
import com.shiguang.market.item.entity.Item;
import com.shiguang.market.item.mapper.ItemMapper;
import com.shiguang.market.lostfound.entity.LostFound;
import com.shiguang.market.lostfound.mapper.LostFoundMapper;
import com.shiguang.market.user.entity.User;
import com.shiguang.market.user.mapper.UserMapper;
import com.shiguang.market.report.service.ReportService;
import com.shiguang.market.admin.service.AuditLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 举报服务实现类
 *
 * @author gugu
 */

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportMapper reportMapper;
    private final NotificationProducer notificationProducer;
    private final AuditLogger auditLogger;
    private final ItemMapper itemMapper;
    private final LostFoundMapper lostFoundMapper;
    private final UserMapper userMapper;

    /**
     * 提交举报
     */
    @Override
    public void submit(Long reporterId, SubmitReportRequest request) {
        if (!java.util.Set.of("ITEM", "LOST_FOUND", "USER").contains(request.getTargetType())) {
            throw new BusinessException(400, "举报对象类型无效");
        }
        if (request.getRiskLevel() != null && !java.util.Set.of("LOW", "MEDIUM", "HIGH").contains(request.getRiskLevel())) {
            throw new BusinessException(400, "风险等级无效");
        }
        Report report = new Report();
        report.setReporterId(reporterId);
        report.setTargetType(request.getTargetType());
        report.setTargetId(request.getTargetId());
        report.setReason(request.getReason());
        report.setRiskLevel(request.getRiskLevel() != null ? request.getRiskLevel() : ReportStatus.RISK_MEDIUM);
        report.setDescription(request.getDescription());
        report.setStatus(ReportStatus.PENDING);
        report.setCreateTime(LocalDateTime.now());
        report.setUpdateTime(LocalDateTime.now());
        reportMapper.insert(report);
    }

    /**
     * 分页查询举报列表（管理员）
     */
    @Override
    public IPage<ReportResponse> pageQuery(String status, Integer pageNum, Integer pageSize) {
        Page<Report> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Report> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.hasText(status), Report::getStatus, status);
        wrapper.orderByAsc(Report::getStatus)
                .orderByDesc(Report::getCreateTime);

        Page<Report> result = reportMapper.selectPage(page, wrapper);

        Page<ReportResponse> responsePage = new Page<>(pageNum, pageSize, result.getTotal());
        responsePage.setRecords(result.getRecords().stream().map(r -> {
            ReportResponse resp = new ReportResponse();
            BeanUtil.copyProperties(r, resp);
            enrichTarget(resp);
            return resp;
        }).toList());
        return responsePage;
    }

    /**
     * 处理举报（管理员）
     */
    @Override
    public void handle(Long reviewerId, Long reportId, HandleReportRequest request) {
        if (!java.util.Set.of(ReportStatus.RESOLVED, ReportStatus.DISMISSED).contains(request.getStatus())) {
            throw new BusinessException(400, "处理结果无效");
        }
        Report report = reportMapper.selectById(reportId);
        if (report == null) {
            throw new BusinessException(404, "举报记录不存在");
        }
        if (!ReportStatus.PENDING.equals(report.getStatus())) {
            throw new BusinessException(400, "该举报已处理，不能重复处理");
        }
        report.setStatus(request.getStatus());
        report.setReviewerId(reviewerId);
        report.setReviewNote(request.getReviewNote());
        report.setUpdateTime(LocalDateTime.now());
        reportMapper.updateById(report);
        auditLogger.log("HANDLE_REPORT", "REPORT", reportId,
                "处理结果：" + request.getStatus() + (request.getReviewNote() == null ? "" : "，备注：" + request.getReviewNote()));

        // 异步通知举报人处理结果
        ReportNotificationMessage notification = new ReportNotificationMessage(
                report.getReporterId(),
                request.getStatus(),
                report.getTargetType(),
                report.getTargetId(),
                request.getReviewNote());
        notificationProducer.sendReportNotification(notification);
    }

    /**
     * 分页查询用户举报列表
     */
    @Override
    public IPage<ReportResponse> getMyReports(Long userId, Integer pageNum, Integer pageSize) {
        Page<Report> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Report> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Report::getReporterId, userId)
               .orderByDesc(Report::getCreateTime);

        Page<Report> result = reportMapper.selectPage(page, wrapper);

        Page<ReportResponse> responsePage = new Page<>(pageNum, pageSize, result.getTotal());
        responsePage.setRecords(result.getRecords().stream().map(r -> {
            ReportResponse resp = new ReportResponse();
            BeanUtil.copyProperties(r, resp);
            enrichTarget(resp);
            return resp;
        }).toList());
        return responsePage;
    }

    private void enrichTarget(ReportResponse response) {
        if ("ITEM".equals(response.getTargetType())) {
            Item item = itemMapper.selectById(response.getTargetId());
            if (item != null) response.setTargetLabel(item.getTitle());
        } else if ("LOST_FOUND".equals(response.getTargetType())) {
            LostFound record = lostFoundMapper.selectById(response.getTargetId());
            if (record != null) response.setTargetLabel(record.getTitle());
        } else if ("USER".equals(response.getTargetType())) {
            User user = userMapper.selectById(response.getTargetId());
            if (user != null) response.setTargetLabel(user.getNickname());
        }
    }
}
