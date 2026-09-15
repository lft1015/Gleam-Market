package com.shiguang.market.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 管理后台仪表盘响应
 *
 * @author gugu
 */
@Data
@Schema(description = "管理后台仪表盘")
public class DashboardResponse {

    @Schema(description = "用户总数")
    private Long totalUsers;

    @Schema(description = "商品总数")
    private Long totalItems;

    @Schema(description = "待审核数")
    private Long pendingReviews;

    @Schema(description = "待处理举报数")
    private Long pendingReports;
}