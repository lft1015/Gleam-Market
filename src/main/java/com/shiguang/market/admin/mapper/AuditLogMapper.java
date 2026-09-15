package com.shiguang.market.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shiguang.market.admin.entity.AuditLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 管理员操作日志Mapper
 *
 * @author gugu
 */
@Mapper
public interface AuditLogMapper extends BaseMapper<AuditLog> {
}