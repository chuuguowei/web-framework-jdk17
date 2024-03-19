package org.example.exception;

import org.example.model.Result;
import org.example.model.enums.BaseErrorCodeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartException;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.sql.SQLException;

/**
 * 全局异常捕获
 */
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BizException.class)
    public Result<?> handleBizException(BizException e) {
        LOGGER.error("BizException", e);
        return Result.error(e.getCode(), e.getMessage(), e.getData());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        LOGGER.error("MethodArgumentNotValidException", e);
        BindingResult bindingResult = e.getBindingResult();
        StringBuilder sb = new StringBuilder("参数校验失败: ");
        String prefix = "";
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            sb.append(prefix).append(fieldError.getField()).append(" ").append(fieldError.getDefaultMessage());
            prefix = ",";
        }
        sb.append(". ");
        return Result.error(BaseErrorCodeEnum.ACCESS_PARAMS_ERROR, sb.toString());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Result<?> handleIllegalArgumentException(IllegalArgumentException e) {
        LOGGER.error("IllegalArgumentException", e);
        return Result.error(BaseErrorCodeEnum.ACCESS_PARAMS_ERROR);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Result<?> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        LOGGER.error("MissingServletRequestParameterException", e);
        String str = String.format("必填参数: [%s] 为空", e.getParameterName());
        return Result.error(BaseErrorCodeEnum.ACCESS_PARAMS_INCOMPLETE, str);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public Result<?> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        LOGGER.error("MethodArgumentTypeMismatchException", e);
        String str = String.format("参数: [%s] 类型错误", e.getName());
        return Result.error(BaseErrorCodeEnum.ACCESS_PARAMS_ERROR, str);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        LOGGER.error("HttpMessageNotReadableException", e);
        return Result.error(BaseErrorCodeEnum.ACCESS_PARAMS_JSON_INVALID);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Result<?> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        LOGGER.error("HttpRequestMethodNotSupportedException", e);
        String str = String.format("请求方法: [%s] 不支持", e.getMethod());
        return Result.error(BaseErrorCodeEnum.ACCESS_REQUEST_METHOD_ERROR, str);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public Result<?> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e) {
        LOGGER.error("HttpMediaTypeNotSupportedException", e);
        String str = String.format("Content-Type: [%s] 不支持", e.getContentType());
        return Result.error(BaseErrorCodeEnum.ACCESS_CONTENT_TYPE_ERROR, str);
    }

    @ExceptionHandler(MultipartException.class)
    public Result<?> handleMultipartException(MultipartException e) {
        LOGGER.error("MultipartException", e);
        return Result.error(BaseErrorCodeEnum.ACCESS_FILE_ERROR);
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<?> handleIllegalStateException(IllegalStateException e) {
        LOGGER.error("IllegalStateException", e);
        return Result.error(BaseErrorCodeEnum.SYSTEM_ERROR);
    }

    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<?> handleNullPointerException(NullPointerException e) {
        LOGGER.error("NullPointerException", e);
        return Result.error(BaseErrorCodeEnum.SYSTEM_ERROR);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<?> handleOtherException(Exception e) {
        LOGGER.error("handleOtherException", e);
        if (e instanceof SQLException) {
            return Result.error(BaseErrorCodeEnum.INTERNAL_SQL_ERROR);
        }
        if (e instanceof ConnectException) {
            return Result.error(BaseErrorCodeEnum.EXTERNAL_CONNECT_ERROR);
        }
        if (e instanceof SocketTimeoutException) {
            return Result.error(BaseErrorCodeEnum.EXTERNAL_SOCKET_TIMEOUT_ERROR);
        }
        if (e instanceof IOException) {
            return Result.error(BaseErrorCodeEnum.INTERNAL_IO_ERROR);
        }
        return Result.error(BaseErrorCodeEnum.SYSTEM_ERROR);
    }

    /***
     * 获取栈信息
     * @param ex 异常对象
     * @return 栈追踪的完整信息
     */
    private String stackTrace(Exception ex) {
        StringWriter stringWriter = new StringWriter();
        ex.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }

}