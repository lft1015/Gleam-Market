package com.shiguang.market.auth.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * JWT 工具类
 * 负责 Token 的生成、解析和校验
 *
 * @author gugu
 */
@Component
public class JwtUtils {

    /**
     * 密钥（Base64 编码，至少 256 位）
     * 生产环境应放到 application.properties 中
     */
    private static final String SECRET = "YourVeryLongSecretKeyForJWTThatIsAtLeast256BitsLongAndEncodedInBase64==";

    /**
     * Token 过期时间：7天（毫秒）
     */
    private static final long EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000L;

    /**
     * 获取签名密钥
     * 每次签名和解析都用同一个 key，保证一致性
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 生成 JWT Token
     *
     * @param userId 用户ID
     * @param role   角色（USER / ADMIN）
     * @return JWT Token 字符串
     */
    public String generateToken(Long userId, String role) {
        long now = System.currentTimeMillis();

        return Jwts.builder()
                .subject(userId.toString())          // 主题：存用户ID
                .claim("role", role)                  // 自定义字段：存角色
                .issuedAt(new Date(now))              // 签发时间
                .expiration(new Date(now + EXPIRE_TIME)) // 过期时间
                .signWith(getSigningKey())             // 签名
                .compact();                            // 序列化成字符串
    }

    /**
     * 从 Token 中解析出 Claims（载荷数据）
     */
    private Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())   // 用同一个 key 验证签名
                .build()
                .parseSignedClaims(token)       // 解析 Token
                .getPayload();                  // 拿到载荷数据
    }

    /**
     * 从 Token 中提取用户 ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        return Long.parseLong(claims.getSubject());
    }

    /**
     * 从 Token 中提取角色
     */
    public String getRoleFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("role", String.class);
    }

    /**
     * 验证 Token 是否有效
     * 不抛异常 = 有效，抛异常 = 无效
     */
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            // 签名不对 / Token 格式错误
            return false;
        } catch (ExpiredJwtException e) {
            // Token 已过期
            return false;
        } catch (Exception e) {
            // 其他未知异常
            return false;
        }
    }
}