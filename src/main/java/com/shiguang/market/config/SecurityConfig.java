package com.shiguang.market.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shiguang.market.auth.filter.JwtAuthenticationFilter;
import com.shiguang.market.common.Result;
import com.shiguang.market.common.ResultCode;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * 安全配置类
 * 组装 JWT 认证过滤器 + 权限规则 + 异常处理
 *
 * @author gugu
 */

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final ObjectMapper objectMapper;

    /**
     * 安装过滤链
     */
    @Bean
    public SecurityFilterChain springSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)  // 禁用 CSRF 保护（前后端项目采用 JWT 认证）

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))  // 不创建会话（JWT 本身就是无状态的）

                .authorizeHttpRequests(auth -> auth
                    .requestMatchers(
                            "/auth/**",    // 注册/登录接口
                            "/doc.html",    // knife4j文档页
                            "/v3/api-docs/**",    // OpenApi 文档接口
                            "/webjars/**"     // knife4j 静态文档接口
                    ).permitAll()  // 认证接口放行,不需要登录
                    .anyRequest().authenticated()  // 其他请求需要登录
                )

                // 添加 JWT 认证过滤器，确保在 UsernamePasswordAuthenticationFilter 之前
                .addFilterBefore(jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class)

                //未登录、权限不足 -> 返回 JSON (不用默认的重定向到登录页)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authenticationException) ->{
                                writeJson(response, ResultCode.UNAUTHORIZED);
                        })

                        .accessDeniedHandler((request, response, accessDeniedException) ->{
                                writeJson(response, ResultCode.FORBIDDEN);
                        })
                );

        return http.build();
    }

    /**
     * 写入 JSON 响应
     */
    private void writeJson(HttpServletResponse response, ResultCode resultCode) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(resultCode.getCode());
        Result<Void> result = Result.fail(resultCode.getCode(), resultCode.getMessage());
        PrintWriter writer = response.getWriter();
        writer.write(objectMapper.writeValueAsString(result));
        writer.flush();
    }
}
