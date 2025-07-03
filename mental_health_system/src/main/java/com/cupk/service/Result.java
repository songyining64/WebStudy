package com.cupk.service;

import java.util.HashMap;
import java.util.Map;

/**
 * 统一API响应结果封装
 */
public class Result<T> {
    private int code;
    private String msg;
    private T data;
    private long timestamp;
    private Map<String, Object> extras;

    public Result() {
        this.timestamp = System.currentTimeMillis();
    }

    public Result(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * 操作成功，无数据返回
     */
    public static <T> Result<T> success() {
        return new Result<>(ResultCode.SUCCESS.getCode(), "操作成功", null);
    }

    /**
     * 操作成功，有数据返回
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(ResultCode.SUCCESS.getCode(), "操作成功", data);
    }

    /**
     * 操作成功，自定义消息和数据
     */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(ResultCode.SUCCESS.getCode(), message, data);
    }

    /**
     * 操作失败，仅错误消息
     * 此方法与error方法等同，为了兼容性添加
     */
    public static <T> Result<T> fail(String msg) {
        return new Result<>(ResultCode.ERROR.getCode(), msg, null);
    }

    /**
     * 操作失败，仅错误消息
     */
    public static <T> Result<T> error(String msg) {
        return new Result<>(ResultCode.ERROR.getCode(), msg, null);
    }

    /**
     * 操作失败，自定义错误码和消息
     */
    public static <T> Result<T> error(int code, String msg) {
        return new Result<>(code, msg, null);
    }

    /**
     * 参数验证失败
     */
    public static <T> Result<T> validateFailed(String msg) {
        return new Result<>(ResultCode.VALIDATE_FAILED.getCode(), msg, null);
    }

    /**
     * 未登录或token失效
     */
    public static <T> Result<T> unauthorized(String msg) {
        return new Result<>(ResultCode.UNAUTHORIZED.getCode(), msg, null);
    }

    /**
     * 没有操作权限
     */
    public static <T> Result<T> forbidden(String msg) {
        return new Result<>(ResultCode.FORBIDDEN.getCode(), msg, null);
    }

    /**
     * 添加额外信息
     */
    public Result<T> addExtra(String key, Object value) {
        if (extras == null) {
            extras = new HashMap<>();
        }
        extras.put(key, value);
        return this;
    }

    // 常用状态码枚举
    public enum ResultCode {
        SUCCESS(200, "操作成功"),
        ERROR(500, "操作失败"),
        VALIDATE_FAILED(400, "参数检验失败"),
        UNAUTHORIZED(401, "暂未登录或token已经过期"),
        FORBIDDEN(403, "没有相关权限"),
        NOT_FOUND(404, "请求的资源不存在");

        private final int code;
        private final String msg;

        ResultCode(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }

    // Getter and Setter
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, Object> getExtras() {
        return extras;
    }

    public void setExtras(Map<String, Object> extras) {
        this.extras = extras;
    }
}
