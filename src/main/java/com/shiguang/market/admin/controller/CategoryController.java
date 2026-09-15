package com.shiguang.market.admin.controller;

import com.shiguang.market.admin.entity.Category;
import com.shiguang.market.admin.service.CategoryService;
import com.shiguang.market.common.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 商品分类控制器
 *
 * @author gugu
 */
@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * 列出所有分类
     *
     * @return 所有分类
     */
    @GetMapping
    public Result<java.util.List<Category>> list() {
        return Result.ok(categoryService.listAll());
    }

    /**
     * 创建分类
     */
    @PostMapping
    public Result<Void> create(@RequestBody Map<String, Object> body) {
        String name = (String) body.get("name");
        Integer sortOrder = body.get("sortOrder") != null
                ? ((Number) body.get("sortOrder")).intValue() : null;
        categoryService.create(name, sortOrder);
        return Result.ok();
    }

    /**
     * 更新分类
     */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        String name = (String) body.get("name");
        Integer sortOrder = body.get("sortOrder") != null
                ? ((Number) body.get("sortOrder")).intValue() : null;
        categoryService.update(id, name, sortOrder);
        return Result.ok();
    }

    /**
     * 删除分类
     *
     * @param id 分类ID
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return Result.ok();
    }
}