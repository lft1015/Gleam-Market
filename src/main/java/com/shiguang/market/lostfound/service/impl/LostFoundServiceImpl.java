package com.shiguang.market.lostfound.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shiguang.market.common.BusinessException;
import com.shiguang.market.lostfound.dto.LostFoundQueryRequest;
import com.shiguang.market.lostfound.dto.LostFoundResponse;
import com.shiguang.market.lostfound.dto.PublishLostFoundRequest;
import com.shiguang.market.lostfound.entity.LostFound;
import com.shiguang.market.lostfound.mapper.LostFoundMapper;
import com.shiguang.market.lostfound.service.LostFoundService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 失物招领服务实现类
 *
 * @author gugu
 */
@Service
@RequiredArgsConstructor
public class LostFoundServiceImpl implements LostFoundService {

    private final LostFoundMapper lostFoundMapper;

    /**
     * 发布失物招领
     *
     * @param userId  用户ID
     * @param request 发布请求
     */
    @Override
    public void publish(Long userId, PublishLostFoundRequest request) {
        LostFound lostFound = new LostFound();
        lostFound.setUserId(userId);
        lostFound.setTitle(request.getTitle());
        lostFound.setDescription(request.getDescription());
        lostFound.setType(request.getType());
        lostFound.setLocation(request.getLocation());
        lostFound.setLostTime(request.getLostFoundTime());
        lostFound.setContact(request.getContact());
        lostFound.setImages(request.getImages());
        lostFound.setStatus("PENDING");
        lostFound.setCreateTime(LocalDateTime.now());
        lostFound.setUpdateTime(LocalDateTime.now());
        lostFoundMapper.insert(lostFound);
    }

    /**
     * 根据ID查询失物招领
     *
     * @param id 失物招领ID
     * @return 失物招领响应
     */
    @Override
    public LostFoundResponse getById(Long id) {
        LostFound lostFound = lostFoundMapper.selectById(id);
        if (lostFound == null) {
            throw new BusinessException(404, "失物招领信息不存在");
        }
        LostFoundResponse response = new LostFoundResponse();
        BeanUtil.copyProperties(lostFound, response);
        return response;
    }

    /**
     * 更新失物招领状态
     *
     * @param userId 用户ID
     * @param id     失物招领ID
     * @param status 状态
     */
    @Override
    public void updateStatus(Long userId, Long id, String status) {
        LostFound lostFound = lostFoundMapper.selectById(id);
        if (lostFound == null) {
            throw new BusinessException(404, "失物招领信息不存在");
        }
        if (!lostFound.getUserId().equals(userId)) {
            throw new BusinessException(403, "您没有权限更新该失物招领状态");
        }
        lostFound.setStatus(status);
        lostFound.setUpdateTime(LocalDateTime.now());
        lostFoundMapper.updateById(lostFound);
    }

    /**
     * 分页查询失物招领
     *
     * @param request 查询请求
     * @return 分页结果
     */
    @Override
    public IPage<LostFoundResponse> pageQuery(LostFoundQueryRequest request) {
        Page<LostFound> page = new Page<>(request.getPage(), request.getSize());
        LambdaQueryWrapper<LostFound> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.hasText(request.getType()), LostFound::getType, request.getType());
        wrapper.and(StringUtils.hasText(request.getKeyword()),
                w -> w.like(LostFound::getTitle, request.getKeyword())
                      .or()
                      .like(LostFound::getDescription, request.getKeyword()));
        wrapper.eq(StringUtils.hasText(request.getLocation()), LostFound::getLocation, request.getLocation());
        wrapper.ge(request.getStartTime() != null, LostFound::getLostTime, request.getStartTime());
        wrapper.le(request.getEndTime() != null, LostFound::getLostTime, request.getEndTime());
        wrapper.orderByDesc(LostFound::getCreateTime);

        Page<LostFound> result = lostFoundMapper.selectPage(page, wrapper);

        Page<LostFoundResponse> responsePage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        responsePage.setRecords(result.getRecords().stream().map(lf -> {
            LostFoundResponse response = new LostFoundResponse();
            BeanUtil.copyProperties(lf, response);
            return response;
        }).toList());

        return responsePage;
    }
}