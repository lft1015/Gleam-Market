package com.shiguang.market.auth.filter;

import com.shiguang.market.auth.util.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * JWT 认证过滤器
 * 用于验证 JWT 令牌并设置用户上下文
 *
 * @author gugu
 */

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {         //继承 OncePerRequestFilter，确保每个请求只处理一次认证

    // 注入 JwtUtils 组件，构造器注入
    private final JwtUtils jwtUtils;

    /**
     * 处理每个请求
     * 从请求头中提取 Token，验证并设置用户上下文
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // 从请求头中获取 Token
        String token = extractToken(request);

        //Token有效 -> 设置认证信息
        if(StringUtils.hasText(token) && jwtUtils.validateToken(token)) {
            // 从 Token 中提取用户 ID 和角色
            Long userId = jwtUtils.getUserIdFromToken(token);
            String role = jwtUtils.getRoleFromToken(token);

            // 构建认证对象
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(
                            userId,
                            null,
                            List.of(new SimpleGrantedAuthority("ROLE_" + role))
                    );

            //存入 Security上下文
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }

        //放行，继续处理下一个请求
        filterChain.doFilter(request, response);
    }

    /**
     * 从请求头中提取 Token
     */
    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            return header.substring(7); // 移除 "Bearer " 前缀
        }
        return null;
    }
}
