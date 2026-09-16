package com.shiguang.market.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ 配置类
 *
 * @author gugu
 */
@Configuration
@ConditionalOnProperty(prefix = "app.rabbitmq", name = "enabled", havingValue = "true", matchIfMissing = true)
public class RabbitMQConfig {

    /** 通知交换机 */
    public static final String EXCHANGE_NOTIFICATION = "gleam.notification.exchange";

    /** 审核通知队列 */
    public static final String QUEUE_REVIEW = "gleam.review.queue";
    /** 举报通知队列 */
    public static final String QUEUE_REPORT = "gleam.report.queue";
    /** 死信队列 */
    public static final String QUEUE_DEAD = "gleam.dead.queue";

    /** 审核通知路由键 */
    public static final String ROUTING_REVIEW = "gleam.review";
    /** 举报通知路由键 */
    public static final String ROUTING_REPORT = "gleam.report";

    /** 死信交换机 */
    public static final String EXCHANGE_DEAD = "gleam.dead.exchange";
    /** 死信路由键 */
    public static final String ROUTING_DEAD = "gleam.dead";

    @Bean
    public DirectExchange notificationExchange() {
        return new DirectExchange(EXCHANGE_NOTIFICATION, true, false);
    }

    @Bean
    public DirectExchange deadExchange() {
        return new DirectExchange(EXCHANGE_DEAD, true, false);
    }

    @Bean
    public Queue reviewQueue() {
        return QueueBuilder.durable(QUEUE_REVIEW)
                .deadLetterExchange(EXCHANGE_DEAD)
                .deadLetterRoutingKey(ROUTING_DEAD)
                .build();
    }

    @Bean
    public Queue reportQueue() {
        return QueueBuilder.durable(QUEUE_REPORT)
                .deadLetterExchange(EXCHANGE_DEAD)
                .deadLetterRoutingKey(ROUTING_DEAD)
                .build();
    }

    @Bean
    public Queue deadQueue() {
        return QueueBuilder.durable(QUEUE_DEAD).build();
    }

    @Bean
    public Binding reviewBinding() {
        return BindingBuilder.bind(reviewQueue())
                .to(notificationExchange())
                .with(ROUTING_REVIEW);
    }

    @Bean
    public Binding reportBinding() {
        return BindingBuilder.bind(reportQueue())
                .to(notificationExchange())
                .with(ROUTING_REPORT);
    }

    @Bean
    public Binding deadBinding() {
        return BindingBuilder.bind(deadQueue())
                .to(deadExchange())
                .with(ROUTING_DEAD);
    }
}