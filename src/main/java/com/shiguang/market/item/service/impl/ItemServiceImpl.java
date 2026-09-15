package com.shiguang.market.item.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shiguang.market.common.BusinessException;
import com.shiguang.market.item.dto.ItemQueryRequest;
import com.shiguang.market.item.dto.ItemResponse;
import com.shiguang.market.item.dto.PublishItemRequest;
import com.shiguang.market.item.entity.Item;
import com.shiguang.market.item.mapper.ItemMapper;
import com.shiguang.market.item.service.ItemService;
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
        item.setStatus("ON_SALE");
        item.setViewCount(0);
        item.setCreateTime(LocalDateTime.now());
        item.setUpdateTime(LocalDateTime.now());
        itemMapper.insert(item);
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
}