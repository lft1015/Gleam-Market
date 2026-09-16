package com.shiguang.market.item.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shiguang.market.common.BusinessException;
import com.shiguang.market.common.RedisKeyPrefix;
import com.shiguang.market.item.constant.ItemStatus;
import com.shiguang.market.item.dto.ItemQueryRequest;
import com.shiguang.market.item.dto.ItemResponse;
import com.shiguang.market.item.dto.PublishItemRequest;
import com.shiguang.market.item.dto.UpdateItemRequest;
import com.shiguang.market.item.entity.Item;
import com.shiguang.market.item.mapper.ItemMapper;
import com.shiguang.market.item.service.ItemService;
import com.shiguang.market.user.entity.User;
import com.shiguang.market.user.mapper.UserMapper;
import com.shiguang.market.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.concurrent.TimeUnit;

/**
 * 商品服务实现类
 *
 * @author gugu
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemMapper itemMapper;
    private final ReviewService reviewService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final UserMapper userMapper;

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
     * 获取商品详情（带 Redis 缓存 + 浏览量递增）
     *
     * @param itemId 商品ID
     * @return 商品详情
     */
    @Override
    public ItemResponse get(Long itemId) {
        String cacheKey = RedisKeyPrefix.ITEM_DETAIL + itemId;
        String viewKey = RedisKeyPrefix.ITEM_VIEW_COUNT + itemId;

        Object cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            if (RedisKeyPrefix.NULL_PLACEHOLDER.equals(cached)) {
                throw new BusinessException(404, "商品不存在");
            }
            // 异步增加浏览量到 Redis
            redisTemplate.opsForValue().increment(viewKey);
            return (ItemResponse) cached;
        }

        Item item = itemMapper.selectById(itemId);
        if (item == null) {
            redisTemplate.opsForValue().set(cacheKey,
                    RedisKeyPrefix.NULL_PLACEHOLDER,
                    RedisKeyPrefix.NULL_TTL_SECONDS,
                    TimeUnit.SECONDS);
            throw new BusinessException(404, "商品不存在");
        }

        ItemResponse response = new ItemResponse();
        BeanUtil.copyProperties(item, response);
        enrichPublisher(response);
        redisTemplate.opsForValue().set(cacheKey, response,
                RedisKeyPrefix.ITEM_DETAIL_TTL_SECONDS, TimeUnit.SECONDS);

        // 异步增加浏览量
        redisTemplate.opsForValue().increment(viewKey);

        return response;
    }

    /**
     * 定时将 Redis 浏览量同步到数据库（每 5 分钟）
     */
    @Scheduled(fixedDelay = 300000)
    public void syncViewCounts() {
        Set<String> keys = redisTemplate.keys(RedisKeyPrefix.ITEM_VIEW_COUNT + "*");
        if (keys == null || keys.isEmpty()) {
            return;
        }
        for (String key : keys) {
            try {
                Long itemId = Long.parseLong(key.replace(RedisKeyPrefix.ITEM_VIEW_COUNT, ""));
                Object value = redisTemplate.opsForValue().get(key);
                if (value != null) {
                    int increment = Integer.parseInt(value.toString());
                    if (increment > 0) {
                        Item item = itemMapper.selectById(itemId);
                        if (item != null) {
                            item.setViewCount(item.getViewCount() + increment);
                            itemMapper.updateById(item);
                        }
                        redisTemplate.opsForValue().set(key, 0);
                    }
                }
            } catch (Exception e) {
                log.warn("同步浏览量失败：key={}, error={}", key, e.getMessage());
            }
        }
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
        Map<String, Set<String>> transitions = Map.of(
                ItemStatus.ON_SALE, Set.of(ItemStatus.TRADING, ItemStatus.SOLD, ItemStatus.OFF_SHELF),
                ItemStatus.TRADING, Set.of(ItemStatus.ON_SALE, ItemStatus.SOLD, ItemStatus.OFF_SHELF),
                ItemStatus.REJECTED, Set.of(ItemStatus.PENDING_REVIEW),
                ItemStatus.OFF_SHELF, Set.of(ItemStatus.PENDING_REVIEW));
        if (!transitions.getOrDefault(item.getStatus(), Set.of()).contains(status)) {
            throw new BusinessException(400, "不允许从当前状态变更为目标状态");
        }
        item.setStatus(status);
        item.setUpdateTime(LocalDateTime.now());
        itemMapper.updateById(item);
        if (ItemStatus.PENDING_REVIEW.equals(status)) {
            reviewService.createReview(userId, "ITEM", itemId);
        }
        // 删除缓存，下次查询时重新加载
        redisTemplate.delete(RedisKeyPrefix.ITEM_DETAIL + itemId);
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
        if (StringUtils.hasText(request.getStatus()) && Set.of(ItemStatus.ON_SALE, ItemStatus.TRADING).contains(request.getStatus())) {
            wrapper.eq(Item::getStatus, request.getStatus());
        } else {
            wrapper.in(Item::getStatus, ItemStatus.ON_SALE, ItemStatus.TRADING);
        }
        wrapper.and(StringUtils.hasText(request.getKeyword()),
                w -> w.like(Item::getTitle, request.getKeyword())
                      .or()
                      .like(Item::getDescription, request.getKeyword()));
        wrapper.ge(request.getMinPrice() != null, Item::getPrice, request.getMinPrice());
        wrapper.le(request.getMaxPrice() != null, Item::getPrice, request.getMaxPrice());
        wrapper.orderByDesc(Item::getCreateTime);

        Page<Item> result = itemMapper.selectPage(page, wrapper);

        Page<ItemResponse> responsePage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        List<ItemResponse> responses = result.getRecords().stream().map(item -> {
            ItemResponse response = new ItemResponse();
            BeanUtil.copyProperties(item, response);
            return response;
        }).toList();
        enrichPublishers(responses);
        responsePage.setRecords(responses);

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
        if (ItemStatus.SOLD.equals(item.getStatus())) {
            throw new BusinessException(400, "已售出商品不能编辑");
        }
        boolean createReview = !ItemStatus.PENDING_REVIEW.equals(item.getStatus());
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
        item.setStatus(ItemStatus.PENDING_REVIEW);
        item.setUpdateTime(LocalDateTime.now());
        itemMapper.updateById(item);
        if (createReview) reviewService.createReview(userId, "ITEM", itemId);
        // 删除缓存
        redisTemplate.delete(RedisKeyPrefix.ITEM_DETAIL + itemId);
    }

    @Override
    public IPage<ItemResponse> pageByOwner(Long userId, ItemQueryRequest request) {
        Page<Item> page = new Page<>(request.getPage(), request.getSize());
        LambdaQueryWrapper<Item> wrapper = new LambdaQueryWrapper<Item>()
                .eq(Item::getUserId, userId)
                .eq(StringUtils.hasText(request.getStatus()), Item::getStatus, request.getStatus())
                .orderByDesc(Item::getUpdateTime);
        Page<Item> result = itemMapper.selectPage(page, wrapper);
        Page<ItemResponse> response = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        response.setRecords(result.getRecords().stream().map(item -> {
            ItemResponse value = new ItemResponse();
            BeanUtil.copyProperties(item, value);
            return value;
        }).toList());
        enrichPublishers(response.getRecords());
        return response;
    }

    private void enrichPublisher(ItemResponse response) {
        User user = userMapper.selectById(response.getUserId());
        if (user != null) {
            response.setPublisherNickname(user.getNickname());
            response.setPublisherAvatar(user.getAvatar());
        }
    }

    private void enrichPublishers(List<ItemResponse> responses) {
        if (responses.isEmpty()) return;
        List<Long> ids = responses.stream().map(ItemResponse::getUserId).distinct().toList();
        Map<Long, User> users = userMapper.selectBatchIds(ids).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));
        responses.forEach(response -> {
            User user = users.get(response.getUserId());
            if (user != null) {
                response.setPublisherNickname(user.getNickname());
                response.setPublisherAvatar(user.getAvatar());
            }
        });
    }
}
