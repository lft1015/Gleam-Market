package com.shiguang.market.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shiguang.market.admin.entity.Category;
import com.shiguang.market.admin.mapper.CategoryMapper;
import com.shiguang.market.admin.service.CategoryService;
import com.shiguang.market.common.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 分类服务实现
 *
 * @author gugu
 */
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;

    /**
     * 查询所有分类
     *
     * @return 所类列表
     */
    @Override
    public List<Category> listAll() {
        return categoryMapper.selectList(
                new LambdaQueryWrapper<Category>()
                        .orderByAsc(Category::getSortOrder));
    }

    /**
     * 创建分类
     *
     * @param name 分类名称
     * @param sortOrder 排序
     */
    @Override
    public void create(String name, Integer sortOrder) {
        boolean exists = categoryMapper.exists(
                new LambdaQueryWrapper<Category>().eq(Category::getName, name));
        if (exists) {
            throw new BusinessException(400, "分类名称已存在");
        }
        Category category = new Category();
        category.setName(name);
        category.setSortOrder(sortOrder != null ? sortOrder : 0);
        category.setCreateTime(LocalDateTime.now());
        category.setUpdateTime(LocalDateTime.now());
        categoryMapper.insert(category);
    }

    /**
     * 更新分类
     *
     * @param id 分类ID
     * @param name 分类名称
     * @param sortOrder 排序
     */
    @Override
    public void update(Long id, String name, Integer sortOrder) {
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException(404, "分类不存在");
        }
        if (!category.getName().equals(name)) {
            boolean exists = categoryMapper.exists(
                    new LambdaQueryWrapper<Category>().eq(Category::getName, name));
            if (exists) {
                throw new BusinessException(400, "分类名称已存在");
            }
        }
        category.setName(name);
        category.setSortOrder(sortOrder != null ? sortOrder : category.getSortOrder());
        category.setUpdateTime(LocalDateTime.now());
        categoryMapper.updateById(category);
    }

    /**
     * 删除分类
     *
     * @param id 分类ID
     */
    @Override
    public void delete(Long id) {
        if (categoryMapper.selectById(id) == null) {
            throw new BusinessException(404, "分类不存在");
        }
        categoryMapper.deleteById(id);
    }
}