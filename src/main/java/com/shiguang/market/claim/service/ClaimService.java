package com.shiguang.market.claim.service;

import com.shiguang.market.claim.dto.ClaimSubmitRequest;
import com.shiguang.market.claim.dto.ClaimResponse;
import com.shiguang.market.claim.entity.Claim;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

public interface ClaimService {

    void submit(Long claimantId, Long lostFoundId, ClaimSubmitRequest request);

    List<Claim> listByLostFound(Long lostFoundId);

    IPage<ClaimResponse> pageByClaimant(Long claimantId, String status, Integer page, Integer size);

    IPage<ClaimResponse> pageAll(String status, Integer page, Integer size);

    void approve(Long reviewerId, Long claimId, String reviewNote);

    void reject(Long reviewerId, Long claimId, String reviewNote);
}
