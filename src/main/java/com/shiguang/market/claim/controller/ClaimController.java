package com.shiguang.market.claim.controller;

import com.shiguang.market.claim.dto.ClaimSubmitRequest;
import com.shiguang.market.claim.entity.Claim;
import com.shiguang.market.claim.service.ClaimService;
import com.shiguang.market.common.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ClaimController {

    private final ClaimService claimService;

    @PostMapping("/lost-found/{lostFoundId}/claims")
    public Result<Void> submit(@PathVariable Long lostFoundId,
                               @Valid @RequestBody ClaimSubmitRequest request) {
        Long userId = (Long) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        claimService.submit(userId, lostFoundId, request);
        return Result.ok();
    }

    @GetMapping("/admin/lost-found/{lostFoundId}/claims")
    public Result<List<Claim>> list(@PathVariable Long lostFoundId) {
        return Result.ok(claimService.listByLostFound(lostFoundId));
    }

    @PutMapping("/admin/claims/{claimId}/approve")
    public Result<Void> approve(@PathVariable Long claimId,
                                @RequestBody Map<String, String> body) {
        Long reviewerId = (Long) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        claimService.approve(reviewerId, claimId, body.get("reviewNote"));
        return Result.ok();
    }

    @PutMapping("/admin/claims/{claimId}/reject")
    public Result<Void> reject(@PathVariable Long claimId,
                               @RequestBody Map<String, String> body) {
        Long reviewerId = (Long) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        claimService.reject(reviewerId, claimId, body.get("reviewNote"));
        return Result.ok();
    }
}