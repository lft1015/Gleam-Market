package com.shiguang.market.claim.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shiguang.market.claim.dto.ClaimSubmitRequest;
import com.shiguang.market.claim.entity.Claim;
import com.shiguang.market.claim.mapper.ClaimMapper;
import com.shiguang.market.claim.service.ClaimService;
import com.shiguang.market.common.BusinessException;
import com.shiguang.market.lostfound.entity.LostFound;
import com.shiguang.market.lostfound.mapper.LostFoundMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClaimServiceImpl implements ClaimService {

    private final ClaimMapper claimMapper;
    private final LostFoundMapper lostFoundMapper;

    @Override
    public void submit(Long claimantId, Long lostFoundId, ClaimSubmitRequest request) {
        LostFound lostFound = lostFoundMapper.selectById(lostFoundId);
        if (lostFound == null) {
            throw new BusinessException(404, "失物招领不存在");
        }

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
    }
}