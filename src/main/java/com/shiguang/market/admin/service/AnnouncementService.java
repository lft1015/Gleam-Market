package com.shiguang.market.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.shiguang.market.admin.entity.Announcement;

/**
 * 公告服务
 *
 * @author gugu
 */
public interface AnnouncementService {

    /**
     * 分页查询公告
     *
     * @param page 页码
     * @param size 每页数量
     * @return 公告分页结果
     */
    IPage<Announcement> pageQuery(Integer page, Integer size);

    /**
     * 创建公告
     *
     * @param title 公告标题
     * @param content 公告内容
     */
    void create(String title, String content);

    /**
     * 更新公告
     *
     * @param id 公告ID
     * @param title 公告标题
     * @param content 公告内容
     * @param isActive 是否激活
     */
    void update(Long id, String title, String content, Boolean isActive);

    /**
     * 删除公告
     *
     * @param id 公告ID
     */
    void delete(Long id);

    /**
     * 分页查询激活公告
     *
     * @param page 页码
     * @param size 每页数量
     * @return 激活公告分页结果
     */
    IPage<Announcement> listActive(Integer page, Integer size);
}