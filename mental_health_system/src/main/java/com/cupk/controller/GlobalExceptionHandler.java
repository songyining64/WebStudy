package com.cupk.controller;

import com.cupk.service.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 处理所有未捕获的异常
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        // 生产环境可隐藏详细异常信息
        return Result.error("服务器异常：" + e.getMessage());
    }

    // 可扩展：处理自定义业务异常
    // @ExceptionHandler(BizException.class)
    // public Result<?> handleBizException(BizException e) {
    // return Result.error(e.getMessage());
    // }
}