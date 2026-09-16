package com.shiguang.market.favorite.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.shiguang.market.BaseTest;
import com.shiguang.market.favorite.entity.Favorite;
import com.shiguang.market.favorite.mapper.FavoriteMapper;
import com.shiguang.market.item.dto.ItemResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("收藏服务集成测试")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class FavoriteServiceTest extends BaseTest {

    @Autowired
    private FavoriteService favoriteService;

    @Autowired
    private FavoriteMapper favoriteMapper;

    @Test
    @DisplayName("添加收藏成功")
    void addFavorite_success() {
        favoriteService.addFavorite(1L, 2L);

        boolean exists = favoriteMapper.exists(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Favorite>()
                        .eq(Favorite::getUserId, 1L)
                        .eq(Favorite::getItemId, 2L));
        assertTrue(exists);
    }

    @Test
    @DisplayName("取消收藏成功")
    void removeFavorite_success() {
        favoriteService.addFavorite(1L, 1L);
        favoriteService.removeFavorite(1L, 1L);

        boolean exists = favoriteMapper.exists(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Favorite>()
                        .eq(Favorite::getUserId, 1L)
                        .eq(Favorite::getItemId, 1L));
        assertFalse(exists);
    }

    @Test
    @DisplayName("检查是否已收藏 - 未收藏")
    void isFavorited_false() {
        boolean result = favoriteService.isFavorited(1L, 2L);
        assertFalse(result);
    }

    @Test
    @DisplayName("检查是否已收藏 - 已收藏")
    void isFavorited_true() {
        favoriteService.addFavorite(1L, 1L);
        boolean result = favoriteService.isFavorited(1L, 1L);
        assertTrue(result);
    }

    @Test
    @DisplayName("获取用户收藏列表")
    void getUserFavorites_success() {
        favoriteService.addFavorite(1L, 1L);
        IPage<ItemResponse> page = favoriteService.getUserFavorites(1L, 1, 10);
        assertTrue(page.getTotal() >= 1);
    }

    @Test
    @DisplayName("获取用户收藏列表 - 空列表")
    void getUserFavorites_empty() {
        IPage<ItemResponse> page = favoriteService.getUserFavorites(999L, 1, 10);
        assertEquals(0, page.getTotal());
    }
}