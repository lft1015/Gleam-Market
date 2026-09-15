package com.shiguang.market.lostfound.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.shiguang.market.lostfound.dto.LostFoundQueryRequest;
import com.shiguang.market.lostfound.dto.LostFoundResponse;
import com.shiguang.market.lostfound.dto.PublishLostFoundRequest;

/**
 * 失物招领服务接口
 */

public interface LostFoundService {
    /**
     * 发布失物招领
     *
     * @param userId 用户ID
     * @param request 发布请求
     */
    void publish(Long userId, PublishLostFoundRequest request);

    /**
     * 根据ID查询失物招领
     *
     * @param id 失物招领ID
     * @return 失物招领响应
     */
    LostFoundResponse getById(Long id);

    /**
     * 更新失物招领状态
     *
     * @param userId 用户ID
     * @param id 失物招领ID
     * @param status 状态
     */
    void updateStatus(Long userId, Long id, String status);

    /**
     * 分页查询失物招领
     *
     * @param request 查询请求
     * @return 分页结果
     */
    IPage<LostFoundResponse> pageQuery(LostFoundQueryRequest request);
}