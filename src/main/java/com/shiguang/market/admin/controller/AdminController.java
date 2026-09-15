package com.shiguang.market.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.shiguang.market.admin.dto.DashboardResponse;
import com.shiguang.market.admin.dto.UserAdminResponse;
import com.shiguang.market.admin.dto.UserStatusRequest;
import com.shiguang.market.admin.service.AdminService;
import com.shiguang.market.common.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员控制器
 *
 * @author gugu
 */

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    /**
     * 获取管理员仪表盘数据
     *
     * @return 仪表盘数据
     */
    @GetMapping("/dashboard")
    public Result<DashboardResponse> dashboard() {
        return Result.ok(adminService.getDashboard());
    }

    /**
     * 分页查询用户列表
     *
     * @param page 页码
     * @param size 每页数量
     * @param keyword 搜索关键词
     * @return 用户列表
     */
    @GetMapping("/users")
    public Result<IPage<UserAdminResponse>> listUsers(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {
        return Result.ok(adminService.listUsers(page, size, keyword));
    }

    /**
     * 修改用户状态（警告/禁言/封禁/恢复）
     *
     * @param userId 用户ID
     * @param request 状态修改请求
     * @return 状态修改结果
     */
    @PutMapping("/users/{userId}/status")
    public Result<Void> updateUserStatus(
            @PathVariable Long userId,
            @Valid @RequestBody UserStatusRequest request) {
        Long adminId = (Long) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        adminService.updateUserStatus(adminId, userId, request);
        return Result.ok();
    }
}