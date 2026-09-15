package com.shiguang.market.report.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shiguang.market.report.entity.Report;
import org.apache.ibatis.annotations.Mapper;

/**
 * 举报Mapper接口
 *
 * @author gugu
 */
@Mapper
public interface ReportMapper extends BaseMapper<Report> {
}