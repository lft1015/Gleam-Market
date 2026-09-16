package com.shiguang.market.user.service;

import com.shiguang.market.BaseTest;
import com.shiguang.market.common.BusinessException;
import com.shiguang.market.user.dto.UpdateProfileRequest;
import com.shiguang.market.user.dto.UserProfileResponse;
import com.shiguang.market.user.entity.User;
import com.shiguang.market.user.mapper.UserMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("用户服务集成测试")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class UserServiceTest extends BaseTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @Test
    @DisplayName("获取用户资料成功")
    void getProfile_success() {
        UserProfileResponse profile = userService.getProfile(1L);
        assertNotNull(profile);
        assertEquals("testuser", profile.getUsername());
        assertNotNull(profile.getNickname());
        assertNotNull(profile.getRole());
        assertEquals("ACTIVE", profile.getStatus());
    }

    @Test
    @DisplayName("获取用户资料失败 - 不存在")
    void getProfile_notFound() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> userService.getProfile(9999L));
        assertEquals(404, ex.getCode());
    }

    @Test
    @DisplayName("更新个人信息成功")
    void updateProfile_success() {
        UpdateProfileRequest req = new UpdateProfileRequest();
        req.setNickname("newNick");
        req.setEmail("new@test.com");

        userService.updateProfile(1L, req);

        User u = userMapper.selectById(1L);
        assertEquals("newNick", u.getNickname());
        assertEquals("new@test.com", u.getEmail());
    }
}