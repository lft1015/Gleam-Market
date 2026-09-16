package com.shiguang.market.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shiguang.market.admin.dto.DashboardResponse;
import com.shiguang.market.admin.dto.UserAdminResponse;
import com.shiguang.market.admin.dto.UserStatusRequest;
import com.shiguang.market.admin.entity.AuditLog;
import com.shiguang.market.admin.mapper.AuditLogMapper;
import com.shiguang.market.admin.service.AdminService;
import com.shiguang.market.common.BusinessException;
import com.shiguang.market.review.entity.Review;
import com.shiguang.market.review.mapper.ReviewMapper;
import com.shiguang.market.item.mapper.ItemMapper;
import com.shiguang.market.report.entity.Report;
import com.shiguang.market.report.mapper.ReportMapper;
import com.shiguang.market.user.entity.User;
import com.shiguang.market.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * 管理员服务实现类
 *
 * @author gugu
 */
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserMapper userMapper;
    private final ItemMapper itemMapper;
    private final ReviewMapper reviewMapper;
    private final ReportMapper reportMapper;
    private final AuditLogMapper auditLogMapper;

    /**
     * 获取管理员仪表盘数据
     *
     * @return 仪表盘数据
     */
    @Override
    public DashboardResponse getDashboard() {
        DashboardResponse dashboard = new DashboardResponse();
        dashboard.setTotalUsers(userMapper.selectCount(null));
        dashboard.setTotalItems(itemMapper.selectCount(null));
        dashboard.setPendingReviews(reviewMapper.selectCount(
                new LambdaQueryWrapper<Review>().eq(Review::getStatus, "PENDING")));
        dashboard.setPendingReports(reportMapper.selectCount(
                new LambdaQueryWrapper<Report>().eq(Report::getStatus, "PENDING")));
        return dashboard;
    }

    /**
     * 分页查询用户列表
     *
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @param keyword 搜索关键词
     * @return 用户列表
     */
    @Override
    public IPage<UserAdminResponse> listUsers(Integer pageNum, Integer pageSize, String keyword) {
        Page<User> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(keyword), User::getUsername, keyword)
                .or().like(StringUtils.hasText(keyword), User::getNickname, keyword)
                .orderByDesc(User::getCreateTime);

        Page<User> result = userMapper.selectPage(page, wrapper);

        Page<UserAdminResponse> responsePage = new Page<>(pageNum, pageSize, result.getTotal());
        responsePage.setRecords(result.getRecords().stream().map(u -> {
            UserAdminResponse resp = new UserAdminResponse();
            BeanUtil.copyProperties(u, resp);
            return resp;
        }).toList());
        return responsePage;
    }

    /**
     * 修改用户状态（警告/禁言/封禁/恢复）
     *
     * @param adminId 管理员ID
     * @param userId 用户ID
     * @param request 状态修改请求
     */
    @Override
    public void updateUserStatus(Long adminId, Long userId, UserStatusRequest request) {
        if (!Set.of("ACTIVE", "WARNED", "MUTED", "BANNED").contains(request.getStatus())) {
            throw new BusinessException(400, "用户状态无效");
        }
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        String oldStatus = user.getStatus();
        user.setStatus(request.getStatus());
        user.setBanUntil(request.getBanUntil());
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);

        AuditLog log = new AuditLog();
        log.setAdminId(adminId);
        log.setAction("UPDATE_USER_STATUS");
        log.setTargetType("USER");
        log.setTargetId(userId);
        log.setDetail(String.format("用户 %s 状态从 %s 改为 %s，备注：%s",
                user.getUsername(), oldStatus, request.getStatus(),
                StringUtils.hasText(request.getRemark()) ? request.getRemark() : "无"));
        log.setCreateTime(LocalDateTime.now());
        auditLogMapper.insert(log);
    }
}
