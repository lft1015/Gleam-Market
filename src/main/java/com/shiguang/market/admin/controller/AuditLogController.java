package com.shiguang.market.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shiguang.market.admin.entity.AuditLog;
import com.shiguang.market.admin.mapper.AuditLogMapper;
import com.shiguang.market.common.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/audit-logs")
@RequiredArgsConstructor
public class AuditLogController {
    private final AuditLogMapper auditLogMapper;

    @GetMapping
    public Result<IPage<AuditLog>> list(@RequestParam(defaultValue = "1") Integer page,
                                        @RequestParam(defaultValue = "15") Integer size,
                                        @RequestParam(required = false) String action) {
        return Result.ok(auditLogMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<AuditLog>()
                        .like(StringUtils.hasText(action), AuditLog::getAction, action)
                        .orderByDesc(AuditLog::getCreateTime)));
    }
}
