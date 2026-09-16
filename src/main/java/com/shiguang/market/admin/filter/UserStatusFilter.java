package com.shiguang.market.admin.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shiguang.market.admin.constant.UserStatus;
import com.shiguang.market.common.Result;
import com.shiguang.market.user.entity.User;
import com.shiguang.market.user.mapper.UserMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * 用户状态过滤器
 *
 * @author gugu
 */
@Component
@RequiredArgsConstructor
@Order(2)
public class UserStatusFilter extends OncePerRequestFilter {

    private final UserMapper userMapper;
    private final ObjectMapper objectMapper;

    private static final Set<String> MUTED_BLOCK_PATHS = Set.of(
            "/items", "/lost-found", "/messages", "/reports", "/favorites"
    );
    private static final Set<String> BYPASS_PATHS = Set.of(
            "/auth", "/doc.html", "/v3/api-docs", "/webjars"
    );

    /**
     * 处理请求
     *
     * @param request 请求对象
     * @param response 响应对象
     * @param filterChain 过滤链
     * @throws IOException
     * @throws ServletException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws IOException, ServletException {
        try {
            String path = request.getRequestURI().substring(request.getContextPath().length());
            for (String bypass : BYPASS_PATHS) {
                if (path.startsWith(bypass)) {
                    filterChain.doFilter(request, response);
                    return;
                }
            }

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !(auth.getPrincipal() instanceof Long userId)) {
                filterChain.doFilter(request, response);
                return;
            }

            User user = userMapper.selectById(userId);
            if (user == null) {
                filterChain.doFilter(request, response);
                return;
            }

            String status = user.getStatus();

            if (UserStatus.BANNED.equals(status)) {
                LocalDateTime banUntil = user.getBanUntil();
                if (banUntil != null && banUntil.isBefore(LocalDateTime.now())) {
                    user.setStatus(UserStatus.ACTIVE);
                    user.setBanUntil(null);
                    userMapper.updateById(user);
                    filterChain.doFilter(request, response);
                    return;
                }
                writeJson(response, 403, "账号已被封禁" +
                        (banUntil != null ? "，解封时间：" + banUntil : "（永久封禁）"));
                return;
            }

            if (UserStatus.MUTED.equals(status)) {
                String method = request.getMethod();
                if (HttpMethod.POST.matches(method) || HttpMethod.PUT.matches(method)
                        || HttpMethod.DELETE.matches(method)) {
                    for (String blockPath : MUTED_BLOCK_PATHS) {
                        if (path.startsWith(blockPath)) {
                            writeJson(response, 403, "账号已被禁言，无法发布内容");
                            return;
                        }
                    }
                }
            }

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            filterChain.doFilter(request, response);
        }
    }

    /**
     * 写入 JSON 响应
     *
     * @param response 响应对象
     * @param code 状态码
     * @param msg 状态信息
     * @param msg 状态信息
     * @throws IOException
     */
    private void writeJson(HttpServletResponse response, int code, String msg) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(code);
        PrintWriter writer = response.getWriter();
        writer.write(objectMapper.writeValueAsString(Result.fail(code, msg)));
        writer.flush();
    }
}
