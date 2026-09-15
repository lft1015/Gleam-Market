package com.shiguang.market.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shiguang.market.admin.entity.Announcement;
import com.shiguang.market.admin.mapper.AnnouncementMapper;
import com.shiguang.market.admin.service.AnnouncementService;
import com.shiguang.market.common.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 公告服务实现类
 *
 * @author gugu
 */
@Service
@RequiredArgsConstructor
public class AnnouncementServiceImpl implements AnnouncementService {

    private final AnnouncementMapper announcementMapper;

    /**
     * 分页查询公告
     *
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 公告分页结果
     */
    @Override
    public IPage<Announcement> pageQuery(Integer pageNum, Integer pageSize) {
        Page<Announcement> page = new Page<>(pageNum, pageSize);
        return announcementMapper.selectPage(page,
                new LambdaQueryWrapper<Announcement>()
                        .orderByDesc(Announcement::getCreateTime));
    }

    /**
     * 创建公告
     *
     * @param title 公告标题
     * @param content 公告内容
     */
    @Override
    public void create(String title, String content) {
        Announcement announcement = new Announcement();
        announcement.setTitle(title);
        announcement.setContent(content);
        announcement.setIsActive(true);
        announcement.setCreateTime(LocalDateTime.now());
        announcement.setUpdateTime(LocalDateTime.now());
        announcementMapper.insert(announcement);
    }

    /**
     * 更新公告
     *
     * @param id 公告ID
     * @param title 公告标题
     * @param content 公告内容
     * @param isActive 是否激活
     */
    @Override
    public void update(Long id, String title, String content, Boolean isActive) {
        Announcement announcement = announcementMapper.selectById(id);
        if (announcement == null) {
            throw new BusinessException(404, "公告不存在");
        }
        announcement.setTitle(title);
        announcement.setContent(content);
        announcement.setIsActive(isActive);
        announcement.setUpdateTime(LocalDateTime.now());
        announcementMapper.updateById(announcement);
    }

    /**
     * 删除公告
     *
     * @param id 公告ID
     */
    @Override
    public void delete(Long id) {
        if (announcementMapper.selectById(id) == null) {
            throw new BusinessException(404, "公告不存在");
        }
        announcementMapper.deleteById(id);
    }

    /**
     * 分页查询激活公告
     *
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 激活公告分页结果
     */
    @Override
    public IPage<Announcement> listActive(Integer pageNum, Integer pageSize) {
        Page<Announcement> page = new Page<>(pageNum, pageSize);
        return announcementMapper.selectPage(page,
                new LambdaQueryWrapper<Announcement>()
                        .eq(Announcement::getIsActive, true)
                        .orderByDesc(Announcement::getCreateTime));
    }
}