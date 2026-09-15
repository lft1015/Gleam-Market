package com.shiguang.market.user.controller;

import com.shiguang.market.common.Result;
import com.shiguang.market.user.dto.UpdateProfileRequest;
import com.shiguang.market.user.dto.UserProfileResponse;
import com.shiguang.market.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 *
 * @author gugu
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 获取当前用户资料
     */
    @GetMapping("/me")
    public Result<UserProfileResponse> getProfile() {
        Long userId = (Long) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return Result.ok(userService.getProfile(userId));
    }

    /**
     * 修改当前用户资料
     */
    @PutMapping("/me")
    public Result<Void> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        Long userId = (Long) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        userService.updateProfile(userId, request);
        return Result.ok();
    }
}