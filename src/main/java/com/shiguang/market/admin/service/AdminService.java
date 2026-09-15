package com.shiguang.market.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.shiguang.market.admin.dto.DashboardResponse;
import com.shiguang.market.admin.dto.UserAdminResponse;
import com.shiguang.market.admin.dto.UserStatusRequest;

/**
 * 管理员服务接口
 *
 * @author gugu
 */

public interface AdminService {

    /**
     * 仪表盘统计
     */
    DashboardResponse getDashboard();

    /**
     * 分页查询用户列表
     */
    IPage<UserAdminResponse> listUsers(Integer page, Integer size, String keyword);

    /**
     * 修改用户状态（警告/禁言/封禁/恢复）
     */
    void updateUserStatus(Long adminId, Long userId, UserStatusRequest request);
}