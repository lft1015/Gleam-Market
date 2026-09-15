package com.shiguang.market.favorite.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shiguang.market.common.BusinessException;
import com.shiguang.market.favorite.entity.Favorite;
import com.shiguang.market.favorite.mapper.FavoriteMapper;
import com.shiguang.market.favorite.service.FavoriteService;
import com.shiguang.market.item.dto.ItemResponse;
import com.shiguang.market.item.entity.Item;
import com.shiguang.market.item.mapper.ItemMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 收藏 服务层实现类
 *
 * @author gugu
 */
@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteMapper favoriteMapper;
    private final ItemMapper itemMapper;

    /**
     * 添加收藏（幂等：重复收藏不报错）
     */
    @Override
    public void addFavorite(Long userId, Long itemId) {
        Item item = itemMapper.selectById(itemId);
        if (item == null) {
            throw new BusinessException(404, "商品不存在");
        }

        boolean exists = favoriteMapper.exists(
                new LambdaQueryWrapper<Favorite>()
                        .eq(Favorite::getUserId, userId)
                        .eq(Favorite::getItemId, itemId)
        );
        if (exists) {
            return;
        }

        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setItemId(itemId);
        favorite.setCreateTime(LocalDateTime.now());
        favoriteMapper.insert(favorite);
    }

    /**
     * 取消收藏
     */
    @Override
    public void removeFavorite(Long userId, Long itemId) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId)
                .eq(Favorite::getItemId, itemId);
        Favorite favorite = favoriteMapper.selectOne(wrapper);
        if (favorite == null) {
            throw new BusinessException(404, "收藏记录不存在");
        }
        favoriteMapper.deleteById(favorite.getId());
    }

    /**
     * 判断是否已收藏
     */
    @Override
    public boolean isFavorited(Long userId, Long itemId) {
        return favoriteMapper.exists(
                new LambdaQueryWrapper<Favorite>()
                        .eq(Favorite::getUserId, userId)
                        .eq(Favorite::getItemId, itemId)
        );
    }

    /**
     * 分页查询我的收藏（返回商品列表）
     */
    @Override
    public IPage<ItemResponse> getUserFavorites(Long userId, Integer pageNum, Integer pageSize) {
        Page<Favorite> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId)
                .orderByDesc(Favorite::getCreateTime);

        Page<Favorite> result = favoriteMapper.selectPage(page, wrapper);

        List<Long> itemIds = result.getRecords().stream()
                .map(Favorite::getItemId)
                .toList();

        if (itemIds.isEmpty()) {
            Page<ItemResponse> empty = new Page<>(pageNum, pageSize, 0);
            empty.setRecords(List.of());
            return empty;
        }

        List<Item> items = itemMapper.selectBatchIds(itemIds);
        Map<Long, Item> itemMap = items.stream()
                .collect(Collectors.toMap(Item::getId, i -> i));

        List<ItemResponse> responses = itemIds.stream()
                .map(itemMap::get)
                .filter(item -> item != null)
                .map(item -> {
                    ItemResponse resp = new ItemResponse();
                    BeanUtil.copyProperties(item, resp);
                    return resp;
                })
                .toList();

        Page<ItemResponse> responsePage = new Page<>(pageNum, pageSize, result.getTotal());
        responsePage.setRecords(responses);
        return responsePage;
    }

    /**
     * 获取商品被收藏数
     */
    @Override
    public Long getFavoriteCount(Long itemId) {
        return favoriteMapper.selectCount(
                new LambdaQueryWrapper<Favorite>()
                        .eq(Favorite::getItemId, itemId)
        );
    }
}