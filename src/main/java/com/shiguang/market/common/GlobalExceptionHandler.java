package com.shiguang.market.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * 拦截所有 Controller 抛出的异常，统一转成 Result 返回
 *
 * @author gugu
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 处理BusinessException 业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException e) {
        return Result.fail(e.getCode(), e.getMessage());
    }

    /**
     * 处理参数校验失败异常（@Valid 触发的）
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handleValidationException(MethodArgumentNotValidException e) {
        // 获取校验失败的所有错误信息
        String message = e.getBindingResult()   // 获取校验失败的所有错误信息
                .getAllErrors()  // 获取所有错误信息
                .stream()       // 转换为流
                .findFirst()   // 取第一条错误
                .map(error -> error.getDefaultMessage())  // 拿到错误文案
                .orElse("参数校验失败");   // 兜底
        return Result.fail(400, message);
    }

    /**
     * 处理其他所有未预期的异常（兜底）
     */
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        log.error("系统异常：", e);  // 打印完整堆栈，方便排查
        return Result.fail(500, "服务器内部错误");
    }
}