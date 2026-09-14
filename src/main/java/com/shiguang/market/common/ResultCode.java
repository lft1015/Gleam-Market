package com.shiguang.market.common;

import lombok.Getter;

/**
 * 结果状态码枚举
 * 用于表示接口调用的结果状态码
 *
 * @author gugu
 */
@Getter
public enum ResultCode {
    /**
     * 成功
     */
    SUCCESS(200, "成功"),

    /**
     * 请求参数错误
     */
    BAD_REQUEST(400, "请求参数错误"),

    /**
     * 未授权
     */
    UNAUTHORIZED(401, "未授权"),

    /**
     * 无权限
     */
    FORBIDDEN(403, "无权限"),

    /**
     * 未找到资源
     */
    NOT_FOUND(404, "未找到资源"),

    /**
     * 内部服务器错误
     */
    INTERNAL_SERVER_ERROR(500, "内部服务器错误");

    private final Integer code;     // 状态码
    private final String message;   // 状态描述

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
