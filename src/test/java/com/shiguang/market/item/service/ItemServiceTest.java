package com.shiguang.market.item.service;

import com.shiguang.market.BaseTest;
import com.shiguang.market.common.BusinessException;
import com.shiguang.market.item.constant.ItemStatus;
import com.shiguang.market.item.dto.ItemQueryRequest;
import com.shiguang.market.item.dto.ItemResponse;
import com.shiguang.market.item.dto.PublishItemRequest;
import com.shiguang.market.item.dto.UpdateItemRequest;
import com.shiguang.market.item.entity.Item;
import com.shiguang.market.item.mapper.ItemMapper;
import com.shiguang.market.review.service.ReviewService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;

@DisplayName("商品服务集成测试")
class ItemServiceTest extends BaseTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemMapper itemMapper;

    @MockBean
    private RedisTemplate<String, Object> redisTemplate;

    @MockBean
    private ReviewService reviewService;

    @Test
    @DisplayName("发布商品成功")
    void publish_success() {
        when(redisTemplate.opsForValue()).thenReturn(mock(ValueOperations.class));

        PublishItemRequest req = new PublishItemRequest();
        req.setTitle("测试商品");
        req.setDescription("这是一个测试商品");
        req.setPrice(new BigDecimal("99.99"));
        req.setOriginalPrice(new BigDecimal("199.99"));
        req.setCategory("电子产品");
        req.setImages("[\"https://example.com/img1.jpg\"]");

        itemService.publish(1L, req);

        Item item = itemMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Item>()
                        .eq(Item::getTitle, "测试商品"));
        assertNotNull(item);
        assertEquals(1L, item.getUserId());
        assertEquals(ItemStatus.PENDING_REVIEW, item.getStatus());
    }

    @Test
    @DisplayName("商品详情 - 命中缓存")
    void get_fromCache() {
        ItemResponse cached = new ItemResponse();
        cached.setId(100L);
        cached.setTitle("缓存商品");
        cached.setPrice(BigDecimal.TEN);

        @SuppressWarnings("unchecked")
        ValueOperations<String, Object> ops = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(ops);
        when(ops.get("item:detail:100")).thenReturn(cached);

        ItemResponse result = itemService.get(100L);
        assertEquals("缓存商品", result.getTitle());
    }

    @Test
    @DisplayName("商品详情 - 不存在抛异常")
    void get_notFound_throwsException() {
        @SuppressWarnings("unchecked")
        ValueOperations<String, Object> ops = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(ops);
        when(ops.get(anyString())).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> itemService.get(9999L));
        assertEquals(404, ex.getCode());
    }

    @Test
    @DisplayName("分页搜索 - 按关键字")
    void pageQuery_byKeyword() {
        when(redisTemplate.opsForValue()).thenReturn(mock(ValueOperations.class));

        PublishItemRequest req = new PublishItemRequest();
        req.setTitle("Java编程思想");
        req.setDescription("计算机经典书籍");
        req.setPrice(new BigDecimal("50"));
        req.setCategory("书籍");
        itemService.publish(1L, req);

        ItemQueryRequest query = new ItemQueryRequest();
        query.setKeyword("Java");
        query.setPage(1);
        query.setSize(10);

        var page = itemService.pageQuery(query);
        assertEquals(1, page.getTotal());
        assertEquals("Java编程思想", page.getRecords().get(0).getTitle());
    }

    @Test
    @DisplayName("分页搜索 - 按价格区间")
    void pageQuery_byPriceRange() {
        when(redisTemplate.opsForValue()).thenReturn(mock(ValueOperations.class));

        PublishItemRequest cheap = new PublishItemRequest();
        cheap.setTitle("便宜货");
        cheap.setPrice(new BigDecimal("5"));
        cheap.setCategory("生活用品");
        itemService.publish(1L, cheap);

        PublishItemRequest expensive = new PublishItemRequest();
        expensive.setTitle("奢侈品");
        expensive.setPrice(new BigDecimal("5000"));
        expensive.setCategory("电子产品");
        itemService.publish(1L, expensive);

        ItemQueryRequest query = new ItemQueryRequest();
        query.setMinPrice(new BigDecimal("1000"));
        query.setMaxPrice(new BigDecimal("10000"));
        query.setPage(1);
        query.setSize(10);

        var page = itemService.pageQuery(query);
        assertEquals(1, page.getTotal());
        assertEquals("奢侈品", page.getRecords().get(0).getTitle());
    }

    @Test
    @DisplayName("编辑商品成功")
    void update_success() {
        when(redisTemplate.opsForValue()).thenReturn(mock(ValueOperations.class));

        PublishItemRequest req = new PublishItemRequest();
        req.setTitle("旧标题");
        req.setPrice(new BigDecimal("10"));
        req.setCategory("书籍");
        itemService.publish(1L, req);

        Item item = itemMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Item>()
                        .eq(Item::getTitle, "旧标题"));

        UpdateItemRequest update = new UpdateItemRequest();
        update.setTitle("新标题");
        update.setPrice(new BigDecimal("20"));
        itemService.update(1L, item.getId(), update);

        Item updated = itemMapper.selectById(item.getId());
        assertEquals("新标题", updated.getTitle());
    }

    @Test
    @DisplayName("编辑失败 - 无权限")
    void update_notOwner_throwsException() {
        when(redisTemplate.opsForValue()).thenReturn(mock(ValueOperations.class));

        PublishItemRequest req = new PublishItemRequest();
        req.setTitle("别人的");
        req.setPrice(new BigDecimal("10"));
        req.setCategory("书籍");
        itemService.publish(1L, req);

        Item item = itemMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Item>()
                        .eq(Item::getTitle, "别人的"));

        UpdateItemRequest update = new UpdateItemRequest();
        update.setTitle("我的");

        BusinessException ex = assertThrows(BusinessException.class,
                () -> itemService.update(2L, item.getId(), update));
        assertEquals(403, ex.getCode());
    }
}