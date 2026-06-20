package com.hjf.entity;

import lombok.Data;
import java.io.Serializable;

/**
 * 全局统一返回结果类
 * @param <T> 泛型，代表 data 字段的具体数据类型
 */
@Data
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    // 状态码常量定义
    public static final int CODE_SUCCESS = 200;
    public static final int CODE_ERROR = 500;

    private Integer code;      // 状态码
    private String message;    // 提示信息
    private T data;            // 响应数据

    // 私有化构造方法，强制使用静态工厂方法创建对象
    private Result() {}

    private Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 成功返回结果 (带数据)
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(CODE_SUCCESS, "操作成功", data);
    }

    /**
     * 成功返回结果 (自定义消息 + 数据)
     */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(CODE_SUCCESS, message, data);
    }

    /**
     * 成功返回结果 (无数据，仅提示成功)
     */
    public static <T> Result<T> success() {
        return new Result<>(CODE_SUCCESS, "操作成功", null);
    }

    /**
     * 失败返回结果 (默认系统异常)
     */
    public static <T> Result<T> fail() {
        return new Result<>(CODE_ERROR, "系统繁忙，请稍后重试", null);
    }

    /**
     * 失败返回结果 (自定义错误消息)
     */
    public static <T> Result<T> fail(String message) {
        return new Result<>(CODE_ERROR, message, null);
    }

    /**
     * 失败返回结果 (自定义状态码 + 错误消息)
     */
    public static <T> Result<T> fail(Integer code, String message) {
        return new Result<>(code, message, null);
    }
}