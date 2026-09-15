package com.shiguang.market.user.service;

import com.shiguang.market.user.dto.UpdateProfileRequest;
import com.shiguang.market.user.dto.UserProfileResponse;

/**
 * 用户服务接口
 *
 * @author gugu
 */
public interface UserService {

    /**
     * 获取用户资料
     *
     * @param userId 用户ID
     * @return 用户资料
     */
    UserProfileResponse getProfile(Long userId);

    /**
     * 修改用户资料
     *
     * @param userId  用户ID
     * @param request 修改请求
     */
    void updateProfile(Long userId, UpdateProfileRequest request);
}