package com.shiguang.market.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.shiguang.market.admin.entity.Announcement;
import com.shiguang.market.admin.service.AnnouncementService;
import com.shiguang.market.common.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 管理员公告接口
 *
 * @author gugu
 */
@RestController
@RequestMapping("/admin/announcements")
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementService announcementService;

    @GetMapping
    public Result<IPage<Announcement>> list(@RequestParam(defaultValue = "1") Integer page,
                                            @RequestParam(defaultValue = "10") Integer size) {
        return Result.ok(announcementService.pageQuery(page, size));
    }

    @PostMapping
    public Result<Void> create(@RequestBody Map<String, String> body) {
        announcementService.create(body.get("title"), body.get("content"));
        return Result.ok();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        announcementService.update(id,
                (String) body.get("title"),
                (String) body.get("content"),
                body.get("isActive") != null ? (Boolean) body.get("isActive") : null);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        announcementService.delete(id);
        return Result.ok();
    }
}