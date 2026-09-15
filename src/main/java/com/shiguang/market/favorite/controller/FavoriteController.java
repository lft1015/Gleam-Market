package com.shiguang.market.favorite.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.shiguang.market.common.Result;
import com.shiguang.market.favorite.service.FavoriteService;
import com.shiguang.market.item.dto.ItemResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * 收藏 控制层
 *
 * @author gugu
 */
@RestController
@RequestMapping("/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    /**
     * 添加收藏
     */
    @PostMapping("/{itemId}")
    public Result<Void> add(@PathVariable Long itemId) {
        Long userId = (Long) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        favoriteService.addFavorite(userId, itemId);
        return Result.ok();
    }

    /**
     * 取消收藏
     */
    @DeleteMapping("/{itemId}")
    public Result<Void> remove(@PathVariable Long itemId) {
        Long userId = (Long) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        favoriteService.removeFavorite(userId, itemId);
        return Result.ok();
    }

    /**
     * 判断是否已收藏
     */
    @GetMapping("/check/{itemId}")
    public Result<Boolean> check(@PathVariable Long itemId) {
        Long userId = (Long) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return Result.ok(favoriteService.isFavorited(userId, itemId));
    }

    /**
     * 获取商品收藏数
     */
    @GetMapping("/count/{itemId}")
    public Result<Long> count(@PathVariable Long itemId) {
        return Result.ok(favoriteService.getFavoriteCount(itemId));
    }

    /**
     * 分页查询我的收藏（返回商品列表）
     */
    @GetMapping
    public Result<IPage<ItemResponse>> list(@RequestParam(defaultValue = "1") Integer page,
                                            @RequestParam(defaultValue = "10") Integer size) {
        Long userId = (Long) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return Result.ok(favoriteService.getUserFavorites(userId, page, size));
    }
}