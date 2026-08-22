package com.skiagent.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一返回结果 DTO
 * @param <T> 数据类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {
    private int code;
    private String message;
    private T data;

    /** 成功返回 */
    public static <T> Result<T> ok(T data) {
        return new Result<>(200, "success", data);
    }
    public static <T> Result<T> ok() {
        return ok(null);
    }

    /** 失败返回 */
    public static <T> Result<T> fail(int code, String message) {
        return new Result<>(code, message, null);
    }
    public static <T> Result<T> fail(String message) {
        return fail(500, message);
    }
}