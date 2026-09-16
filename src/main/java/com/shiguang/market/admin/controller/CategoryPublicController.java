package com.shiguang.market.admin.controller;

import com.shiguang.market.admin.entity.Category;
import com.shiguang.market.admin.service.CategoryService;
import com.shiguang.market.common.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryPublicController {
    private final CategoryService categoryService;

    @GetMapping
    public Result<List<Category>> list() {
        return Result.ok(categoryService.listAll());
    }
}
