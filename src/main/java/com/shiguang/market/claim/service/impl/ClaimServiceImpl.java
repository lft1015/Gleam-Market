package com.shiguang.market.claim.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shiguang.market.claim.dto.ClaimSubmitRequest;
import com.shiguang.market.claim.dto.ClaimResponse;
import com.shiguang.market.claim.entity.Claim;
import com.shiguang.market.claim.mapper.ClaimMapper;
import com.shiguang.market.claim.service.ClaimService;
import com.shiguang.market.common.BusinessException;
import com.shiguang.market.lostfound.entity.LostFound;
import com.shiguang.market.lostfound.mapper.LostFoundMapper;
import com.shiguang.market.lostfound.constant.LostFoundStatus;
import com.shiguang.market.user.entity.User;
import com.shiguang.market.user.mapper.UserMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.hutool.core.bean.BeanUtil;
import org.springframework.util.StringUtils;
import com.shiguang.market.admin.service.AuditLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClaimServiceImpl implements ClaimService {

    private final ClaimMapper claimMapper;
    private final LostFoundMapper lostFoundMapper;
    private final UserMapper userMapper;
    private final AuditLogger auditLogger;

    @Override
    public void submit(Long claimantId, Long lostFoundId, ClaimSubmitRequest request) {
        LostFound lostFound = lostFoundMapper.selectById(lostFoundId);
        if (lostFound == null) {
            throw new BusinessException(404, "失物招领不存在");
        }
        if (!java.util.Set.of(LostFoundStatus.IN_PROGRESS, LostFoundStatus.PROCESSING).contains(lostFound.getStatus())) {
            throw new BusinessException(400, "该失物信息当前不能认领");
        }
        boolean exists = claimMapper.exists(new LambdaQueryWrapper<Claim>()
                .eq(Claim::getLostFoundId, lostFoundId)
                .eq(Claim::getClaimantId, claimantId)
                .eq(Claim::getStatus, "PENDING"));
        if (exists) throw new BusinessException(400, "您已提交过待处理的认领申请");

        Claim claim = new Claim();
        claim.setLostFoundId(lostFoundId);
        claim.setClaimantId(claimantId);
        claim.setMessage(request.getMessage());
        claim.setContact(request.getContact());
        claim.setVerification(request.getVerification());
        claim.setStatus("PENDING");
        claim.setCreateTime(LocalDateTime.now());
        claim.setUpdateTime(LocalDateTime.now());
        claimMapper.insert(claim);
    }

    @Override
    public List<Claim> listByLostFound(Long lostFoundId) {
        return claimMapper.selectList(
                new LambdaQueryWrapper<Claim>()
                        .eq(Claim::getLostFoundId, lostFoundId)
                        .orderByDesc(Claim::getCreateTime));
    }

    @Override
    public IPage<ClaimResponse> pageByClaimant(Long claimantId, String status, Integer pageNum, Integer pageSize) {
        return page(new LambdaQueryWrapper<Claim>()
                .eq(Claim::getClaimantId, claimantId)
                .eq(StringUtils.hasText(status), Claim::getStatus, status)
                .orderByDesc(Claim::getCreateTime), pageNum, pageSize);
    }

    @Override
    public IPage<ClaimResponse> pageAll(String status, Integer pageNum, Integer pageSize) {
        return page(new LambdaQueryWrapper<Claim>()
                .eq(StringUtils.hasText(status), Claim::getStatus, status)
                .orderByAsc(Claim::getStatus)
                .orderByDesc(Claim::getCreateTime), pageNum, pageSize);
    }

    @Override
    public void approve(Long reviewerId, Long claimId, String reviewNote) {
        Claim claim = claimMapper.selectById(claimId);
        if (claim == null) {
            throw new BusinessException(404, "认领申请不存在");
        }
        if (!"PENDING".equals(claim.getStatus())) {
            throw new BusinessException(400, "该申请已处理");
        }
        claim.setStatus("APPROVED");
        claim.setReviewerId(reviewerId);
        claim.setReviewNote(reviewNote);
        claim.setUpdateTime(LocalDateTime.now());
        claimMapper.updateById(claim);
        auditLogger.log("APPROVE_CLAIM", "CLAIM", claimId, reviewNote);
        LostFound lostFound = lostFoundMapper.selectById(claim.getLostFoundId());
        if (lostFound != null && LostFoundStatus.IN_PROGRESS.equals(lostFound.getStatus())) {
            lostFound.setStatus(LostFoundStatus.PROCESSING);
            lostFound.setUpdateTime(LocalDateTime.now());
            lostFoundMapper.updateById(lostFound);
        }
    }

    @Override
    public void reject(Long reviewerId, Long claimId, String reviewNote) {
        Claim claim = claimMapper.selectById(claimId);
        if (claim == null) {
            throw new BusinessException(404, "认领申请不存在");
        }
        if (!"PENDING".equals(claim.getStatus())) {
            throw new BusinessException(400, "该申请已处理");
        }
        claim.setStatus("REJECTED");
        claim.setReviewerId(reviewerId);
        claim.setReviewNote(reviewNote);
        claim.setUpdateTime(LocalDateTime.now());
        claimMapper.updateById(claim);
        auditLogger.log("REJECT_CLAIM", "CLAIM", claimId, reviewNote);
    }

    private IPage<ClaimResponse> page(LambdaQueryWrapper<Claim> wrapper, Integer pageNum, Integer pageSize) {
        Page<Claim> result = claimMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        Page<ClaimResponse> response = new Page<>(pageNum, pageSize, result.getTotal());
        response.setRecords(result.getRecords().stream().map(claim -> {
            ClaimResponse dto = new ClaimResponse();
            BeanUtil.copyProperties(claim, dto);
            LostFound lostFound = lostFoundMapper.selectById(claim.getLostFoundId());
            if (lostFound != null) {
                dto.setLostFoundTitle(lostFound.getTitle());
                dto.setLostFoundType(lostFound.getType());
            }
            User user = userMapper.selectById(claim.getClaimantId());
            if (user != null) dto.setClaimantNickname(user.getNickname());
            return dto;
        }).toList());
        return response;
    }
}
