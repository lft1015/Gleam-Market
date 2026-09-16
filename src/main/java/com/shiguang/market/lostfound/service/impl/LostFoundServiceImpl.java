package com.shiguang.market.lostfound.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shiguang.market.common.BusinessException;
import com.shiguang.market.lostfound.constant.LostFoundStatus;
import com.shiguang.market.lostfound.dto.LostFoundQueryRequest;
import com.shiguang.market.lostfound.dto.LostFoundResponse;
import com.shiguang.market.lostfound.dto.PublishLostFoundRequest;
import com.shiguang.market.lostfound.dto.UpdateLostFoundRequest;
import com.shiguang.market.lostfound.entity.LostFound;
import com.shiguang.market.lostfound.mapper.LostFoundMapper;
import com.shiguang.market.lostfound.service.LostFoundService;
import com.shiguang.market.review.service.ReviewService;
import com.shiguang.market.user.entity.User;
import com.shiguang.market.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 失物招领服务实现类
 *
 * @author gugu
 */
@Service
@RequiredArgsConstructor
public class LostFoundServiceImpl implements LostFoundService {

    private final LostFoundMapper lostFoundMapper;
    private final ReviewService reviewService;
    private final UserMapper userMapper;

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
        lostFound.setStatus(LostFoundStatus.PENDING_REVIEW);
        lostFound.setCreateTime(LocalDateTime.now());
        lostFound.setUpdateTime(LocalDateTime.now());
        lostFoundMapper.insert(lostFound);

        reviewService.createReview(userId, "LOST_FOUND", lostFound.getId());
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
        enrichPublisher(response);
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
        Map<String, Set<String>> transitions = Map.of(
                LostFoundStatus.IN_PROGRESS, Set.of(LostFoundStatus.PROCESSING, LostFoundStatus.FOUND, LostFoundStatus.RETURNED, LostFoundStatus.CLOSED),
                LostFoundStatus.PROCESSING, Set.of(LostFoundStatus.IN_PROGRESS, LostFoundStatus.FOUND, LostFoundStatus.RETURNED, LostFoundStatus.CLOSED),
                LostFoundStatus.REJECTED, Set.of(LostFoundStatus.PENDING_REVIEW));
        if (!transitions.getOrDefault(lostFound.getStatus(), Set.of()).contains(status)) {
            throw new BusinessException(400, "不允许从当前状态变更为目标状态");
        }
        lostFound.setStatus(status);
        lostFound.setUpdateTime(LocalDateTime.now());
        lostFoundMapper.updateById(lostFound);
        if (LostFoundStatus.PENDING_REVIEW.equals(status)) {
            reviewService.createReview(userId, "LOST_FOUND", id);
        }
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
        wrapper.in(LostFound::getStatus, LostFoundStatus.IN_PROGRESS, LostFoundStatus.PROCESSING);
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
        List<LostFoundResponse> responses = result.getRecords().stream().map(lf -> {
            LostFoundResponse response = new LostFoundResponse();
            BeanUtil.copyProperties(lf, response);
            return response;
        }).toList();
        enrichPublishers(responses);
        responsePage.setRecords(responses);

        return responsePage;
    }

    @Override
    public IPage<LostFoundResponse> pageByOwner(Long userId, LostFoundQueryRequest request) {
        Page<LostFound> page = new Page<>(request.getPage(), request.getSize());
        LambdaQueryWrapper<LostFound> wrapper = new LambdaQueryWrapper<LostFound>()
                .eq(LostFound::getUserId, userId)
                .eq(StringUtils.hasText(request.getType()), LostFound::getType, request.getType())
                .orderByDesc(LostFound::getUpdateTime);
        Page<LostFound> result = lostFoundMapper.selectPage(page, wrapper);
        Page<LostFoundResponse> response = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        response.setRecords(result.getRecords().stream().map(value -> {
            LostFoundResponse dto = new LostFoundResponse();
            BeanUtil.copyProperties(value, dto);
            return dto;
        }).toList());
        enrichPublishers(response.getRecords());
        return response;
    }

    @Override
    public void update(Long userId, Long id, UpdateLostFoundRequest request) {
        LostFound value = lostFoundMapper.selectById(id);
        if (value == null) throw new BusinessException(404, "失物招领信息不存在");
        if (!value.getUserId().equals(userId)) throw new BusinessException(403, "只能编辑自己的失物信息");
        if (Set.of(LostFoundStatus.FOUND, LostFoundStatus.RETURNED, LostFoundStatus.CLOSED).contains(value.getStatus())) {
            throw new BusinessException(400, "已完成或关闭的信息不能编辑");
        }
        boolean createReview = !LostFoundStatus.PENDING_REVIEW.equals(value.getStatus());
        value.setTitle(request.getTitle());
        value.setDescription(request.getDescription());
        value.setImages(request.getImages());
        value.setType(request.getType());
        value.setLocation(request.getLocation());
        value.setContact(request.getContact());
        value.setLostTime(request.getLostFoundTime());
        value.setStatus(LostFoundStatus.PENDING_REVIEW);
        value.setUpdateTime(LocalDateTime.now());
        lostFoundMapper.updateById(value);
        if (createReview) reviewService.createReview(userId, "LOST_FOUND", id);
    }

    private void enrichPublisher(LostFoundResponse response) {
        User user = userMapper.selectById(response.getUserId());
        if (user != null) {
            response.setPublisherNickname(user.getNickname());
            response.setPublisherAvatar(user.getAvatar());
        }
    }

    private void enrichPublishers(List<LostFoundResponse> responses) {
        if (responses.isEmpty()) return;
        Map<Long, User> users = userMapper.selectBatchIds(
                responses.stream().map(LostFoundResponse::getUserId).distinct().toList())
                .stream().collect(Collectors.toMap(User::getId, Function.identity()));
        responses.forEach(response -> {
            User user = users.get(response.getUserId());
            if (user != null) {
                response.setPublisherNickname(user.getNickname());
                response.setPublisherAvatar(user.getAvatar());
            }
        });
    }
}
