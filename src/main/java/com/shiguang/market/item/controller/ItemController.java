package com.shiguang.market.item.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.shiguang.market.common.Result;
import com.shiguang.market.item.dto.ItemQueryRequest;
import com.shiguang.market.item.dto.ItemResponse;
import com.shiguang.market.item.dto.PublishItemRequest;
import com.shiguang.market.item.service.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * 商品控制器
 *
 * @author gugu
 */

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    // 发布商品
    @PostMapping
    public Result<Void> publish(@Valid @RequestBody PublishItemRequest request) {
        Long userId = (Long) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        itemService.publish(userId, request);
        return Result.ok();
    }

    // 分页查询商品列表
    @GetMapping
    public Result<IPage<ItemResponse>> list(ItemQueryRequest request) {
        return Result.ok(itemService.pageQuery(request));
    }

    // 查看商品详情
    @GetMapping("/{itemId}")
    public Result<ItemResponse> getById(@PathVariable Long itemId) {
        return Result.ok(itemService.get(itemId));
    }

    // 修改商品状态
    @PutMapping("/{itemId}/status")
    public Result<Void> updateStatus(@PathVariable Long itemId,
                                     @RequestParam String status) {
        Long userId = (Long) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        itemService.updateStatus(userId, itemId, status);
        return Result.ok();
    }
}