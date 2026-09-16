package com.shiguang.market;

import org.mockito.Mockito;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import static org.mockito.Mockito.mock;

/**
 * 测试配置：为不可用的中间件提供 Mock Bean，避免 Spring 上下文加载失败
 *
 * @author gugu
 */
@TestConfiguration
public class TestConfig {

    @Bean
    @Primary
    public RedisConnectionFactory redisConnectionFactory() {
        return mock(RedisConnectionFactory.class, Mockito.RETURNS_DEEP_STUBS);
    }

    @Bean
    @Primary
    public RedisTemplate<String, Object> redisTemplate() {
        @SuppressWarnings("unchecked")
        RedisTemplate<String, Object> template = mock(RedisTemplate.class, Mockito.RETURNS_DEEP_STUBS);
        return template;
    }

    @Bean
    @Primary
    public org.springframework.amqp.rabbit.connection.ConnectionFactory rabbitConnectionFactory() {
        return mock(org.springframework.amqp.rabbit.connection.ConnectionFactory.class);
    }

    @Bean
    @Primary
    public RabbitTemplate rabbitTemplate() {
        return mock(RabbitTemplate.class);
    }

    @Bean
    @Primary
    public org.springframework.amqp.rabbit.core.RabbitAdmin rabbitAdmin() {
        return mock(org.springframework.amqp.rabbit.core.RabbitAdmin.class);
    }
}