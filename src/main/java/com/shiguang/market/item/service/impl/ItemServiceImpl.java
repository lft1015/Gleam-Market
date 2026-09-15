package com.shiguang.market.item.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shiguang.market.common.BusinessException;
import com.shiguang.market.item.constant.ItemStatus;
import com.shiguang.market.item.dto.ItemQueryRequest;
import com.shiguang.market.item.dto.ItemResponse;
import com.shiguang.market.item.dto.PublishItemRequest;
import com.shiguang.market.item.dto.UpdateItemRequest;
import com.shiguang.market.item.entity.Item;
import com.shiguang.market.item.mapper.ItemMapper;
import com.shiguang.market.item.service.ItemService;
import com.shiguang.market.config.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 商品服务实现类
 *
 * @author gugu
 */
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemMapper itemMapper;
    private final ReviewService reviewService;

    /**
     * 发布商品
     *
     * @param userId 用户ID
     * @param request 发布商品请求
     */
    @Override
    public void publish(Long userId, PublishItemRequest request) {
        Item item = new Item();
        item.setUserId(userId);
        item.setTitle(request.getTitle());
        item.setDescription(request.getDescription());
        item.setPrice(request.getPrice());
        item.setOriginalPrice(request.getOriginalPrice());
        item.setCategory(request.getCategory());
        item.setImages(request.getImages());
        item.setStatus(ItemStatus.PENDING_REVIEW);
        item.setViewCount(0);
        item.setCreateTime(LocalDateTime.now());
        item.setUpdateTime(LocalDateTime.now());
        itemMapper.insert(item);

        reviewService.createReview(userId, "ITEM", item.getId());
    }

    /**
     * 获取商品详情
     *
     * @param itemId 商品ID
     * @return 商品详情
     */
    @Override
    public ItemResponse get(Long itemId) {
        Item item = itemMapper.selectById(itemId);
        if (item == null) {
            throw new BusinessException(404, "商品不存在");
        }
        ItemResponse response = new ItemResponse();
        BeanUtil.copyProperties(item, response);
        return response;
    }

    /**
     * 更新商品状态
     *
     * @param userId 用户ID
     * @param itemId 商品ID
     * @param status 更新商品状态
     */
    @Override
    public void updateStatus(Long userId, Long itemId, String status) {
        Item item = itemMapper.selectById(itemId);
        if (item == null) {
            throw new BusinessException(404, "商品不存在");
        }
        if (!item.getUserId().equals(userId)) {
            throw new BusinessException(403, "您没有权限更新商品状态");
        }
        item.setStatus(status);
        item.setUpdateTime(LocalDateTime.now());
        itemMapper.updateById(item);
    }

    /**
     * 分页查询商品
     *
     * @param request 查询请求
     * @return 分页结果
     */
    @Override
    public IPage<ItemResponse> pageQuery(ItemQueryRequest request) {
        Page<Item> page = new Page<>(request.getPage(), request.getSize());
        LambdaQueryWrapper<Item> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.hasText(request.getCategory()), Item::getCategory, request.getCategory());
        wrapper.eq(StringUtils.hasText(request.getStatus()), Item::getStatus, request.getStatus());
        wrapper.and(StringUtils.hasText(request.getKeyword()),
                w -> w.like(Item::getTitle, request.getKeyword())
                      .or()
                      .like(Item::getDescription, request.getKeyword()));
        wrapper.ge(request.getMinPrice() != null, Item::getPrice, request.getMinPrice());
        wrapper.le(request.getMaxPrice() != null, Item::getPrice, request.getMaxPrice());
        wrapper.orderByDesc(Item::getCreateTime);

        Page<Item> result = itemMapper.selectPage(page, wrapper);

        Page<ItemResponse> responsePage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        responsePage.setRecords(result.getRecords().stream().map(item -> {
            ItemResponse response = new ItemResponse();
            BeanUtil.copyProperties(item, response);
            return response;
        }).toList());

        return responsePage;
    }

    /**
     * 编辑商品（仅更新非空字段）
     */
    @Override
    public void update(Long userId, Long itemId, UpdateItemRequest request) {
        Item item = itemMapper.selectById(itemId);
        if (item == null) {
            throw new BusinessException(404, "商品不存在");
        }
        if (!item.getUserId().equals(userId)) {
            throw new BusinessException(403, "只能编辑自己的商品");
        }
        if (StringUtils.hasText(request.getTitle())) {
            item.setTitle(request.getTitle());
        }
        if (StringUtils.hasText(request.getDescription())) {
            item.setDescription(request.getDescription());
        }
        if (request.getPrice() != null) {
            item.setPrice(request.getPrice());
        }
        if (request.getOriginalPrice() != null) {
            item.setOriginalPrice(request.getOriginalPrice());
        }
        if (StringUtils.hasText(request.getCategory())) {
            item.setCategory(request.getCategory());
        }
        if (request.getImages() != null) {
            item.setImages(request.getImages());
        }
        item.setUpdateTime(LocalDateTime.now());
        itemMapper.updateById(item);
    }
}