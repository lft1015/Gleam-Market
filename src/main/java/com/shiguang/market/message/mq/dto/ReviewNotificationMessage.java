package com.shiguang.market.message.mq.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 审核通知消息体
 *
 * @author gugu
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewNotificationMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 提交人ID（通知接收者） */
    private Long submitterId;

    /** 审核结果：APPROVED / REJECTED */
    private String decision;

    /** 审核对象类型：ITEM / LOST_FOUND */
    private String targetType;

    /** 审核对象ID */
    private Long targetId;

    /** 审核备注 */
    private String reviewNote;
}