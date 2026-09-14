package com.shiguang.market.common;

import lombok.Data;
/**
 * 结果类
 * 包含状态码、状态信息、数据等
 * 用于统一接口返回格式
 *
 * @author gugu
 */
@Data
public class Result<T> {
    /**
     * 状态码:Http 风格的状态码
     */
    private Integer code;
    /**
     * 状态信息:自定义的状态信息
     */
    private String msg;
    /**
     * 数据:实际返回的数据
     */
    private T data;

    /**
     * 私有全参构造器
     */
    private Result(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    //=======================静态工厂方法=======================

    /**
     * 用于创建成功的结果(默认状态信息)
     * @return 成功的结果
     */
    public static <T> Result<T> ok() {
        return new Result<>(200, "成功", null);
    }

    /**
     * 用于创建成功的结果(返回数据)
     * @param data 要返回的数据
     * @return 成功的结果
     */
    public static <T> Result<T> ok(T data) {
        return new Result<>(200, "成功", data);
    }

    /**
     * 用于创建失败的结果(返回状态信息)
     * @return 失败的结果
     */
    public static <T> Result<T> fail(String msg) {
        return new Result<>(500, msg, null);
    }

    /**
     * 用于创建失败的结果(返回状态码、状态信息和数据)
     * @param code 状态码
     * @param msg 状态信息
     * @return 失败的结果
     */
    public static <T> Result<T> fail(Integer code, String msg) {
        return new Result<>(code, msg, null);
    }
}
