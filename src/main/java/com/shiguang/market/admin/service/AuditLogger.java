package com.shiguang.market.admin.service;

import com.shiguang.market.admin.entity.AuditLog;
import com.shiguang.market.admin.mapper.AuditLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuditLogger {
    private final AuditLogMapper auditLogMapper;

    public void log(String action, String targetType, Long targetId, String detail) {
        if (SecurityContextHolder.getContext().getAuthentication() == null) return;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof Long adminId)) return;
        AuditLog log = new AuditLog();
        log.setAdminId(adminId);
        log.setAction(action);
        log.setTargetType(targetType);
        log.setTargetId(targetId);
        log.setDetail(detail);
        log.setCreateTime(LocalDateTime.now());
        auditLogMapper.insert(log);
    }
}
