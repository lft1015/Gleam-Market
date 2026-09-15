package com.shiguang.market.admin.service;

import com.shiguang.market.admin.entity.Category;

import java.util.List;

/**
 * 分类服务
 *
 * @author gugu
 */
public interface CategoryService {

    /**
     * 列出所有分类
     *
     * @return 所有分类
     */
    List<Category> listAll();

    /**
     * 创建分类
     *
     * @param name 分类名称
     * @param sortOrder 排序
     * @param sortOrder 排序
     */
    void create(String name, Integer sortOrder);

    /**
     * 更新分类
     *
     * @param id 分类ID
     * @param name 分类名称
     * @param sortOrder 排序
     */
       void update(Long id, String name, Integer sortOrder);

    /**
     * 删除分类
     *
     * @param id 分类ID
     */
    void delete(Long id);
}