package com.shiguang.market.claim.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ClaimResponse {
    private Long id;
    private Long lostFoundId;
    private Long claimantId;
    private String claimantNickname;
    private String lostFoundTitle;
    private String lostFoundType;
    private String message;
    private String contact;
    private String verification;
    private String status;
    private Long reviewerId;
    private String reviewNote;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") private LocalDateTime createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") private LocalDateTime updateTime;
}
