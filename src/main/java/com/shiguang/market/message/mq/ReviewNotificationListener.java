package com.shiguang.market.message.mq;

import com.shiguang.market.config.RabbitMQConfig;
import com.shiguang.market.message.mq.dto.ReviewNotificationMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 审核通知消费者
 * 监听审核队列，异步处理通知逻辑（后续可接入推送/短信）
 *
 * @author gugu
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ReviewNotificationListener {

    /**
     * 消费审核通知
     */
    @RabbitListener(queues = RabbitMQConfig.QUEUE_REVIEW)
    public void handleReviewNotification(ReviewNotificationMessage message) {
        log.info("收到审核通知：submitterId={}, decision={}, targetType={}, targetId={}, note={}",
                message.getSubmitterId(),
                message.getDecision(),
                message.getTargetType(),
                message.getTargetId(),
                message.getReviewNote());
    }
}