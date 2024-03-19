package org.example.exception;

import org.example.model.enums.BaseErrorCodeEnum;

/**
 * 常规业务异常
 */
public class BizException extends RuntimeException {

    private int code;

    private Object data;

    public BizException() {
        super(BaseErrorCodeEnum.SYSTEM_ERROR.getMsg());
        this.code = BaseErrorCodeEnum.SYSTEM_ERROR.getCode();
    }

    public BizException(int code) {
        super();
        this.code = code;
    }

    public BizException(String message) {
        super(message);
        this.code = BaseErrorCodeEnum.SYSTEM_ERROR.getCode();
    }

    public BizException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BizException(String message, Throwable cause) {
        super(message, cause);
        this.code = BaseErrorCodeEnum.SYSTEM_ERROR.getCode();
    }

    public BizException(int code, Object data) {
        super();
        this.code = code;
        this.data = data;
    }

    public BizException(int code, String message, Object data) {
        super(message);
        this.code = code;
        this.data = data;
    }

    public BizException(int code, String message, Object data, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.data = data;
    }

    public BizException(String message, Object data) {
        super(message);
        this.code = BaseErrorCodeEnum.SYSTEM_ERROR.getCode();
        this.data = data;
    }

    public BizException(String message, Object data, Throwable cause) {
        super(message, cause);
        this.code = BaseErrorCodeEnum.SYSTEM_ERROR.getCode();
        this.data = data;
    }

    public BizException(BaseErrorCodeEnum errorCodeEnum) {
        super(errorCodeEnum.getMsg());
        this.code = errorCodeEnum.getCode();
    }

    public BizException(BaseErrorCodeEnum errorCodeEnum, String message) {
        super(message);
        this.code = errorCodeEnum.getCode();
    }

    public int getCode() {
        return code;
    }

    public Object getData() {
        return data;
    }
}
