package com.shiguang.market.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.shiguang.market.admin.entity.Announcement;
import com.shiguang.market.admin.service.AnnouncementService;
import com.shiguang.market.common.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 公开公告接口
 *
 * @author gugu
 */
@RestController
@RequestMapping("/announcements")
@RequiredArgsConstructor
public class AnnouncementPublicController {

    private final AnnouncementService announcementService;

    /**
     * 获取公开公告列表
     *
     * @param page 页码
     * @param size 每页数量
     * @return 公开公告列表
     */
    @GetMapping
    public Result<IPage<Announcement>> list(@RequestParam(defaultValue = "1") Integer page,
                                            @RequestParam(defaultValue = "10") Integer size) {
        return Result.ok(announcementService.listActive(page, size));
    }
}