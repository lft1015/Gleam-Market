package com.shiguang.market.message.mq;

import com.shiguang.market.config.RabbitMQConfig;
import com.shiguang.market.message.mq.dto.ReportNotificationMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 举报处理通知消费者
 * 监听举报队列，异步处理通知逻辑
 *
 * @author gugu
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ReportNotificationListener {

    /**
     * 消费举报处理通知
     */
    @RabbitListener(queues = RabbitMQConfig.QUEUE_REPORT)
    public void handleReportNotification(ReportNotificationMessage message) {
        log.info("收到举报处理通知：reporterId={}, decision={}, targetType={}, targetId={}, note={}",
                message.getReporterId(),
                message.getDecision(),
                message.getTargetType(),
                message.getTargetId(),
                message.getReviewNote());
    }
}