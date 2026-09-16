package com.shiguang.market.admin.service;

import com.shiguang.market.BaseTest;
import com.shiguang.market.admin.entity.Category;
import com.shiguang.market.admin.mapper.CategoryMapper;
import com.shiguang.market.common.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("分类服务集成测试")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class CategoryServiceTest extends BaseTest {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryMapper categoryMapper;

    @Test
    @DisplayName("查询所有分类 - 按排序升序")
    void listAll_success() {
        List<Category> list = categoryService.listAll();
        assertNotNull(list);
        assertTrue(list.size() >= 3);
        assertNotNull(list.get(0).getName());
    }

    @Test
    @DisplayName("创建分类成功")
    void create_success() {
        categoryService.create("运动户外", 10);

        Category c = categoryMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Category>()
                        .eq(Category::getName, "运动户外"));
        assertNotNull(c);
        assertEquals(10, c.getSortOrder());
    }

    @Test
    @DisplayName("更新分类成功")
    void update_success() {
        List<Category> list = categoryMapper.selectList(null);
        Long id = list.get(0).getId();

        categoryService.update(id, "数码产品", 5);

        Category c = categoryMapper.selectById(id);
        assertEquals("数码产品", c.getName());
        assertEquals(5, c.getSortOrder());
    }

    @Test
    @DisplayName("更新分类失败 - 不存在")
    void update_notFound() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> categoryService.update(9999L, "x", 1));
        assertEquals(404, ex.getCode());
    }

    @Test
    @DisplayName("删除分类成功")
    void delete_success() {
        categoryService.create("临时分类", 99);
        Category c = categoryMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Category>()
                        .eq(Category::getName, "临时分类"));
        assertNotNull(c);

        categoryService.delete(c.getId());

        assertNull(categoryMapper.selectById(c.getId()));
    }

    @Test
    @DisplayName("删除分类失败 - 不存在")
    void delete_notFound() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> categoryService.delete(9999L));
        assertEquals(404, ex.getCode());
    }
}