package com.cupk.controller;

import com.cupk.service.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理所有未捕获的异常
     */
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e, HttpServletRequest request) {
        logger.error("未知异常: {}, 请求URL: {}, 请求方法: {}, 参数: {}",
                e.getMessage(),
                request.getRequestURL(),
                request.getMethod(),
                request.getQueryString(),
                e);
        return Result.error("服务器内部错误");
    }

    /**
     * 处理数据库异常
     */
    @ExceptionHandler(SQLException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<?> handleSQLException(SQLException e) {
        logger.error("数据库异常", e);
        return Result.error("数据库操作异常，请联系管理员");
    }

    /**
     * 处理请求参数校验异常(表单提交)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        String message = fieldErrors.stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        logger.warn("参数验证失败: {}", message);
        return Result.validateFailed(message);
    }

    /**
     * 处理请求参数绑定异常
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleBindException(BindException e) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        String message = fieldErrors.stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        logger.warn("参数绑定失败: {}", message);
        return Result.validateFailed(message);
    }

    /**
     * 处理缺少请求参数异常
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleMissingServletRequestParameter(MissingServletRequestParameterException e,
            HttpServletRequest request) {
        logger.warn("参数缺失: {}, 请求URL: {}, 请求方法: {}, 参数: {}",
                e.getParameterName(),
                request.getRequestURL(),
                request.getMethod(),
                request.getQueryString());
        return Result.error("缺少必要参数: " + e.getParameterName());
    }

    /**
     * 处理请求方法不支持异常
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public Result<?> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        logger.warn("不支持的请求方法: {}", e.getMethod());
        return Result.error(405, "不支持的请求方法: " + e.getMethod());
    }

    /**
     * 处理参数类型不匹配异常
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException e,
            HttpServletRequest request) {
        String requiredType = e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : "未知类型";

        logger.warn("参数类型不匹配: {} 应为 {}, 请求URL: {}, 请求方法: {}, 参数: {}",
                e.getName(),
                requiredType,
                request.getRequestURL(),
                request.getMethod(),
                request.getQueryString());

        return Result.error("参数类型不匹配: " + e.getName() + " 应为 " + requiredType);
    }

    /**
     * 处理运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public Result<?> handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        logger.warn("运行时异常: {}, 请求URL: {}, 请求方法: {}, 参数: {}",
                e.getMessage(),
                request.getRequestURL(),
                request.getMethod(),
                request.getQueryString());
        return Result.error(e.getMessage());
    }
}