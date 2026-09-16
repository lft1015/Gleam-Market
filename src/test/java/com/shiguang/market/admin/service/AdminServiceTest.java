package com.shiguang.market.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.shiguang.market.BaseTest;
import com.shiguang.market.admin.dto.DashboardResponse;
import com.shiguang.market.admin.dto.UserAdminResponse;
import com.shiguang.market.admin.dto.UserStatusRequest;
import com.shiguang.market.admin.entity.AuditLog;
import com.shiguang.market.admin.mapper.AuditLogMapper;
import com.shiguang.market.common.BusinessException;
import com.shiguang.market.user.entity.User;
import com.shiguang.market.user.mapper.UserMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("管理员服务集成测试")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class AdminServiceTest extends BaseTest {

    @Autowired
    private AdminService adminService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AuditLogMapper auditLogMapper;

    @Test
    @DisplayName("获取仪表盘数据")
    void getDashboard_success() {
        DashboardResponse dashboard = adminService.getDashboard();
        assertNotNull(dashboard);
        assertTrue(dashboard.getTotalUsers() >= 3);
        assertTrue(dashboard.getTotalItems() >= 2);
        assertTrue(dashboard.getPendingReviews() >= 1);
        assertTrue(dashboard.getPendingReports() >= 1);
    }

    @Test
    @DisplayName("分页查询用户列表 - 全部")
    void listUsers_all() {
        IPage<UserAdminResponse> page = adminService.listUsers(1, 10, null);
        assertNotNull(page);
        assertTrue(page.getTotal() >= 3);
    }

    @Test
    @DisplayName("分页查询用户列表 - 按关键字搜索")
    void listUsers_byKeyword() {
        IPage<UserAdminResponse> page = adminService.listUsers(1, 10, "seller");
        assertEquals(1, page.getTotal());
        assertEquals("seller", page.getRecords().get(0).getUsername());
    }

    @Test
    @DisplayName("修改用户状态成功 - 禁言并记录日志")
    void updateUserStatus_success() {
        UserStatusRequest req = new UserStatusRequest();
        req.setStatus("MUTED");
        req.setRemark("测试禁言");

        adminService.updateUserStatus(2L, 3L, req);

        User user = userMapper.selectById(3L);
        assertEquals("MUTED", user.getStatus());

        Long count = auditLogMapper.selectCount(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AuditLog>()
                        .eq(AuditLog::getTargetId, 3L)
                        .eq(AuditLog::getAction, "UPDATE_USER_STATUS"));
        assertTrue(count >= 1);
    }

    @Test
    @DisplayName("修改用户状态失败 - 用户不存在")
    void updateUserStatus_notFound() {
        UserStatusRequest req = new UserStatusRequest();
        req.setStatus("BANNED");

        BusinessException ex = assertThrows(BusinessException.class,
                () -> adminService.updateUserStatus(2L, 9999L, req));
        assertEquals(404, ex.getCode());
    }
}