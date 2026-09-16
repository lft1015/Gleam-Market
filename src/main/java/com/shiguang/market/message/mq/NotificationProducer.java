package com.shiguang.market.message.mq;

import com.shiguang.market.config.RabbitMQConfig;
import com.shiguang.market.message.mq.dto.ReportNotificationMessage;
import com.shiguang.market.message.mq.dto.ReviewNotificationMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * 通知消息生产者
 *
 * @author gugu
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationProducer {

    private final RabbitTemplate rabbitTemplate;

    /**
     * 发送审核结果通知
     */
    public void sendReviewNotification(ReviewNotificationMessage message) {
        log.info("发送审核通知：submitterId={}, decision={}, targetType={}",
                message.getSubmitterId(), message.getDecision(), message.getTargetType());
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NOTIFICATION,
                RabbitMQConfig.ROUTING_REVIEW,
                message);
    }

    /**
     * 发送举报处理通知
     */
    public void sendReportNotification(ReportNotificationMessage message) {
        log.info("发送举报处理通知：reporterId={}, decision={}",
                message.getReporterId(), message.getDecision());
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NOTIFICATION,
                RabbitMQConfig.ROUTING_REPORT,
                message);
    }
}