package com.shiguang.market.claim.service;

import com.shiguang.market.claim.dto.ClaimSubmitRequest;
import com.shiguang.market.claim.entity.Claim;

import java.util.List;

public interface ClaimService {

    void submit(Long claimantId, Long lostFoundId, ClaimSubmitRequest request);

    List<Claim> listByLostFound(Long lostFoundId);

    void approve(Long reviewerId, Long claimId, String reviewNote);

    void reject(Long reviewerId, Long claimId, String reviewNote);
}