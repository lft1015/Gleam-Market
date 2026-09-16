package com.shiguang.market.claim.service;

import com.shiguang.market.BaseTest;
import com.shiguang.market.claim.dto.ClaimSubmitRequest;
import com.shiguang.market.claim.entity.Claim;
import com.shiguang.market.claim.mapper.ClaimMapper;
import com.shiguang.market.common.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("认领服务集成测试")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class ClaimServiceTest extends BaseTest {

    @Autowired
    private ClaimService claimService;

    @Autowired
    private ClaimMapper claimMapper;

    @Test
    @DisplayName("提交认领申请成功")
    void submit_success() {
        ClaimSubmitRequest req = new ClaimSubmitRequest();
        req.setMessage("test claim message");
        req.setContact("13800138000");
        req.setVerification("test verification");

        claimService.submit(3L, 1L, req);

        List<Claim> claims = claimMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Claim>()
                        .eq(Claim::getLostFoundId, 1L)
                        .eq(Claim::getClaimantId, 3L)
                        .orderByDesc(Claim::getCreateTime));
        assertTrue(claims.size() >= 1);
        assertEquals("PENDING", claims.get(0).getStatus());
    }
}