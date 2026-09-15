package com.shiguang.market.favorite.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.shiguang.market.item.dto.ItemResponse;

/**
 * 收藏 服务层
 *
 * @author gugu
 */
public interface FavoriteService {

    /**
     * 添加收藏（幂等：重复收藏不报错）
     */
    void addFavorite(Long userId, Long itemId);

    /**
     * 取消收藏
     */
    void removeFavorite(Long userId, Long itemId);

    /**
     * 判断是否已收藏
     */
    boolean isFavorited(Long userId, Long itemId);

    /**
     * 分页查询我的收藏（返回商品列表）
     */
    IPage<ItemResponse> getUserFavorites(Long userId, Integer page, Integer size);

    /**
     * 获取商品被收藏数
     */
    Long getFavoriteCount(Long itemId);
}