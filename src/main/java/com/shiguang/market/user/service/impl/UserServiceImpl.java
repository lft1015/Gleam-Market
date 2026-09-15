package com.shiguang.market.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.shiguang.market.common.BusinessException;
import com.shiguang.market.user.dto.UpdateProfileRequest;
import com.shiguang.market.user.dto.UserProfileResponse;
import com.shiguang.market.user.entity.User;
import com.shiguang.market.user.mapper.UserMapper;
import com.shiguang.market.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 用户服务实现类
 *
 * @author gugu
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    /**
     * 获取用户资料
     */
    @Override
    public UserProfileResponse getProfile(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        UserProfileResponse response = new UserProfileResponse();
        BeanUtil.copyProperties(user, response);
        return response;
    }

    /**
     * 修改用户资料（仅更新非空字段）
     */
    @Override
    public void updateProfile(Long userId, UpdateProfileRequest request) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        if (StringUtils.hasText(request.getNickname())) {
            user.setNickname(request.getNickname());
        }
        if (StringUtils.hasText(request.getEmail())) {
            user.setEmail(request.getEmail());
        }
        if (StringUtils.hasText(request.getPhone())) {
            user.setPhone(request.getPhone());
        }
        if (request.getAvatar() != null) {
            user.setAvatar(request.getAvatar());
        }
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
    }
}