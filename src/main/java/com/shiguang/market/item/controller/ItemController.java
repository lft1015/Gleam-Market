package com.shiguang.market.item.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.shiguang.market.common.Result;
import com.shiguang.market.item.dto.ItemQueryRequest;
import com.shiguang.market.item.dto.ItemResponse;
import com.shiguang.market.item.dto.PublishItemRequest;
import com.shiguang.market.item.dto.UpdateItemRequest;
import com.shiguang.market.item.service.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

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

    @GetMapping("/my")
    public Result<IPage<ItemResponse>> mine(ItemQueryRequest request) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return Result.ok(itemService.pageByOwner(userId, request));
    }

    // 编辑商品
    @PutMapping("/{itemId}")
    public Result<Void> update(@PathVariable Long itemId,
                               @Valid @RequestBody UpdateItemRequest request) {
        Long userId = (Long) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        itemService.update(userId, itemId, request);
        return Result.ok();
    }

    // 查看商品详情
    @GetMapping("/{itemId}")
    public Result<ItemResponse> getById(@PathVariable Long itemId) {
        ItemResponse item = itemService.get(itemId);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean admin = auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_SUPER_ADMIN"));
        boolean owner = auth != null && auth.getPrincipal() instanceof Long
                && item.getUserId().equals((Long) auth.getPrincipal());
        if (!owner && !admin && !java.util.Set.of("ON_SALE", "TRADING").contains(item.getStatus())) {
            throw new com.shiguang.market.common.BusinessException(404, "商品不存在或尚未公开");
        }
        return Result.ok(item);
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
