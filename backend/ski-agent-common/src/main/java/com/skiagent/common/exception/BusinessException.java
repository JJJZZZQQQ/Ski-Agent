package com.skiagent.common.exception;

import lombok.Getter;

/**
 * 业务异常类 - 滑雪 Agent 统一异常
 * 用于业务逻辑层的异常抛出，由 GlobalExceptionHandler 统一处理
 */
@Getter
public class BusinessException extends RuntimeException {

    /** HTTP 状态码 */
    private final int code;

    /** 异常消息 */
    private final String message;

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public BusinessException(String message) {
        this(500, message);
    }

    /** 常见错误快捷构造 */
    public static BusinessException badRequest(String message) {
        return new BusinessException(400, message);
    }

    public static BusinessException unauthorized(String message) {
        return new BusinessException(401, message);
    }

    public static BusinessException notFound(String message) {
        return new BusinessException(404, message);
    }
}