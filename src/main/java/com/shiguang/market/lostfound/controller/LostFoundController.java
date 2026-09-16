package com.shiguang.market.lostfound.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.shiguang.market.common.Result;
import com.shiguang.market.lostfound.dto.LostFoundQueryRequest;
import com.shiguang.market.lostfound.dto.LostFoundResponse;
import com.shiguang.market.lostfound.dto.PublishLostFoundRequest;
import com.shiguang.market.lostfound.service.LostFoundService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

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
        return Result.ok(lostFoundService.getById(id));
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