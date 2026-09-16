package com.shiguang.market.lostfound.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.shiguang.market.BaseTest;
import com.shiguang.market.common.BusinessException;
import com.shiguang.market.lostfound.dto.LostFoundQueryRequest;
import com.shiguang.market.lostfound.dto.LostFoundResponse;
import com.shiguang.market.lostfound.dto.PublishLostFoundRequest;
import com.shiguang.market.lostfound.entity.LostFound;
import com.shiguang.market.lostfound.mapper.LostFoundMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("失物招领服务集成测试")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class LostFoundServiceTest extends BaseTest {

    @Autowired
    private LostFoundService lostFoundService;

    @Autowired
    private LostFoundMapper lostFoundMapper;

    @Test
    @DisplayName("发布失物招领成功")
    void publish_success() {
        PublishLostFoundRequest req = new PublishLostFoundRequest();
        req.setTitle("test found item");
        req.setDescription("test description");
        req.setLocation("test location");
        req.setContact("test contact");
        req.setType("FOUND");

        lostFoundService.publish(3L, req);

        LostFound lf = lostFoundMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<LostFound>()
                        .eq(LostFound::getTitle, "test found item"));
        assertNotNull(lf);
    }

    @Test
    @DisplayName("分页查询 - 全部")
    void pageQuery_all() {
        LostFoundQueryRequest req = new LostFoundQueryRequest();
        req.setPage(1);
        req.setSize(10);

        IPage<LostFoundResponse> page = lostFoundService.pageQuery(req);
        assertNotNull(page);
        assertTrue(page.getTotal() >= 1);
    }

    @Test
    @DisplayName("分页查询 - 按关键字")
    void pageQuery_byKeyword() {
        PublishLostFoundRequest r = new PublishLostFoundRequest();
        r.setTitle("keyword test item");
        r.setDescription("desc");
        r.setLocation("loc");
        r.setContact("contact");
        r.setType("FOUND");
        lostFoundService.publish(3L, r);

        LostFoundQueryRequest req = new LostFoundQueryRequest();
        req.setKeyword("keyword");
        req.setPage(1);
        req.setSize(10);

        IPage<LostFoundResponse> page = lostFoundService.pageQuery(req);
        assertTrue(page.getTotal() >= 1);
    }

    @Test
    @DisplayName("根据ID查询详情")
    void getById_success() {
        LostFoundResponse resp = lostFoundService.getById(1L);
        assertNotNull(resp);
        assertNotNull(resp.getTitle());
        assertEquals(1L, resp.getId());
    }

    @Test
    @DisplayName("根据ID查询失败 - 不存在")
    void getById_notFound() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> lostFoundService.getById(9999L));
        assertEquals(404, ex.getCode());
    }

    @Test
    @DisplayName("更新失物招领状态")
    void updateStatus_success() {
        lostFoundService.updateStatus(1L, 1L, "RESOLVED");
        LostFound lf = lostFoundMapper.selectById(1L);
        assertEquals("RESOLVED", lf.getStatus());
    }
}