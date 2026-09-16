package com.shiguang.market.message.mq.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 举报处理通知消息体
 *
 * @author gugu
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportNotificationMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 举报人ID（通知接收者） */
    private Long reporterId;

    /** 处理状态：RESOLVED / DISMISSED */
    private String decision;

    /** 被举报对象类型 */
    private String targetType;

    /** 被举报对象ID */
    private Long targetId;

    /** 处理备注 */
    private String reviewNote;
}