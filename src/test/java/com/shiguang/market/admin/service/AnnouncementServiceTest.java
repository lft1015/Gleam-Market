package com.shiguang.market.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.shiguang.market.BaseTest;
import com.shiguang.market.admin.entity.Announcement;
import com.shiguang.market.admin.mapper.AnnouncementMapper;
import com.shiguang.market.common.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("公告服务集成测试")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class AnnouncementServiceTest extends BaseTest {

    @Autowired
    private AnnouncementService announcementService;

    @Autowired
    private AnnouncementMapper announcementMapper;

    @Test
    @DisplayName("分页查询所有公告")
    void pageQuery_success() {
        IPage<Announcement> page = announcementService.pageQuery(1, 10);
        assertNotNull(page);
        assertTrue(page.getTotal() >= 1);
    }

    @Test
    @DisplayName("创建公告成功")
    void create_success() {
        announcementService.create("新公告", "新公告内容");

        Announcement a = announcementMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Announcement>()
                        .eq(Announcement::getTitle, "新公告"));
        assertNotNull(a);
        assertEquals("新公告内容", a.getContent());
        assertTrue(a.getIsActive());
    }

    @Test
    @DisplayName("更新公告成功")
    void update_success() {
        announcementService.create("待更新", "旧内容");
        Announcement a = announcementMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Announcement>()
                        .eq(Announcement::getTitle, "待更新"));

        announcementService.update(a.getId(), "已更新", "新内容", false);

        Announcement updated = announcementMapper.selectById(a.getId());
        assertEquals("已更新", updated.getTitle());
        assertEquals("新内容", updated.getContent());
        assertFalse(updated.getIsActive());
    }

    @Test
    @DisplayName("更新公告失败 - 不存在")
    void update_notFound() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> announcementService.update(9999L, "x", "x", true));
        assertEquals(404, ex.getCode());
    }

    @Test
    @DisplayName("删除公告成功")
    void delete_success() {
        announcementService.create("待删除", "xxx");
        Announcement a = announcementMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Announcement>()
                        .eq(Announcement::getTitle, "待删除"));

        announcementService.delete(a.getId());

        assertNull(announcementMapper.selectById(a.getId()));
    }

    @Test
    @DisplayName("删除公告失败 - 不存在")
    void delete_notFound() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> announcementService.delete(9999L));
        assertEquals(404, ex.getCode());
    }

    @Test
    @DisplayName("分页查询激活公告")
    void listActive_success() {
        IPage<Announcement> page = announcementService.listActive(1, 10);
        assertNotNull(page);
        page.getRecords().forEach(a -> assertTrue(a.getIsActive()));
    }
}