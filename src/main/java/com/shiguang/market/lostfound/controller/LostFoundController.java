package com.shiguang.market.lostfound.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.shiguang.market.common.Result;
import com.shiguang.market.lostfound.dto.LostFoundQueryRequest;
import com.shiguang.market.lostfound.dto.LostFoundResponse;
import com.shiguang.market.lostfound.dto.PublishLostFoundRequest;
import com.shiguang.market.lostfound.dto.UpdateLostFoundRequest;
import com.shiguang.market.lostfound.service.LostFoundService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

/**
 * 失物招领控制器
 *
 * @author gugu
 */
@RestController
@RequestMapping("/lost-found")
@RequiredArgsConstructor
public class LostFoundController {

    private final LostFoundService lostFoundService;

    /**
     * 分页查询失物招领列表
     */
    @GetMapping
    public Result<IPage<LostFoundResponse>> list(LostFoundQueryRequest request) {
        return Result.ok(lostFoundService.pageQuery(request));
    }

    @GetMapping("/my")
    public Result<IPage<LostFoundResponse>> mine(LostFoundQueryRequest request) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return Result.ok(lostFoundService.pageByOwner(userId, request));
    }

    /**
     * 发布失物招领信息
     */
    @PostMapping
    public Result<Void> publish(@Valid @RequestBody PublishLostFoundRequest request) {
        Long userId = (Long) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        lostFoundService.publish(userId, request);
        return Result.ok();
    }

    /**
     * 查看失物招领详情
     */
    @GetMapping("/{id}")
    public Result<LostFoundResponse> getById(@PathVariable Long id) {
        LostFoundResponse record = lostFoundService.getById(id);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean admin = auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_SUPER_ADMIN"));
        boolean owner = auth != null && auth.getPrincipal() instanceof Long
                && record.getUserId().equals((Long) auth.getPrincipal());
        if (!owner && !admin && !java.util.Set.of("IN_PROGRESS", "PROCESSING").contains(record.getStatus())) {
            throw new com.shiguang.market.common.BusinessException(404, "失物信息不存在或尚未公开");
        }
        return Result.ok(record);
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody UpdateLostFoundRequest request) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        lostFoundService.update(userId, id, request);
        return Result.ok();
    }

    /**
     * 修改失物招领状态
     */
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id,
                                     @RequestParam String status) {
        Long userId = (Long) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        lostFoundService.updateStatus(userId, id, status);
        return Result.ok();
    }
}
