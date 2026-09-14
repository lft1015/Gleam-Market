package com.shiguang.market.common;

/**
 * 业务异常类
 * 用于处理业务逻辑中的异常情况
 *
 * @author gugu
 */
public class BusinessException extends RuntimeException {
    private Integer code;   // 状态码

    /**
     * 构造方法
     * @param code 状态码
     * @param msg 状态信息
     */
    public BusinessException(Integer code, String msg) {
        super(msg);
        this.code = code;
    }

    /**
     * 构造方法
     * @param msg 状态信息
     */
    public BusinessException(String msg) {
        this(500, msg);
    }

    /**
     * 获取状态码
     * @return 状态码
     */
    public Integer getCode() {
        return code;
    }
}
