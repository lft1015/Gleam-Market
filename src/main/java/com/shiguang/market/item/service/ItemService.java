package com.shiguang.market.item.service;

import com.shiguang.market.item.dto.ItemResponse;
import com.shiguang.market.item.dto.PublishItemRequest;

/**
 * 商品服务接口
 *
 * @author gugu
 */
public interface ItemService {

    /**
     * 发布商品
     *
     * @param userId 用户ID
     * @param request 发布商品请求
     */
    void publish(Long userId, PublishItemRequest request);

    /**
     * 获取商品详情
     *
     * @param itemId 商品ID
     * @return 商品详情
     */
    ItemResponse get(Long itemId);

    /**
     * 更新商品状态
     *
     * @param userId 用户ID
     * @param itemId 商品ID
     * @param status 更新商品状态
     */
    void updateStatus(Long userId, Long itemId, String status);
}
