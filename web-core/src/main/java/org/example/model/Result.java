package org.example.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.example.model.enums.BaseErrorCodeEnum;

import java.io.Serializable;
import java.util.UUID;

@Data
@ApiModel(description = "通用响应返回对象")
public class Result<T> implements Serializable {

    /**
     * 返回状态码
     */
    @ApiModelProperty(value = "返回状态码", position = 0)
    private int errno;

    /**
     * 返回信息
     */
    @ApiModelProperty(value = "返回信息", position = 1)
    private String msg;

    /**
     * 返回当时的时间戳，秒为单位
     */
    @ApiModelProperty(value = "时间戳", position = 2)
    private Long timestamp;

    /**
     * 请求request_id，即logid
     */
    @ApiModelProperty(value = "日志ID", position = 3)
    private String requestId;

    /**
     * 返回结果数据
     */
    @ApiModelProperty(value = "结果数据", position = 4)
    private T data;

    public static <T> Result<T> error() {
        return error(500, "fail");
    }

    public static <T> Result<T> error(String msg) {
        return error(500, msg);
    }

    public static <T> Result<T> error(BaseErrorCodeEnum baseErrorCodeEnum) {
        return error(baseErrorCodeEnum.getCode(), baseErrorCodeEnum.getMsg());
    }

    public static <T> Result<T> error(BaseErrorCodeEnum baseErrorCodeEnum, String msg) {
        return error(baseErrorCodeEnum.getCode(), msg);
    }

    public static <T> Result<T> error(Integer status, String msg) {
        Result<T> r = new Result<>();
        r.setErrno(status);
        r.setMsg(msg);
        r.setTimestamp(System.currentTimeMillis() / 1000L);
        r.setRequestId(UUID.randomUUID().toString().replace("-", ""));
        return r;
    }

    public static <T> Result<T> error(Integer status, String msg, T data) {
        Result<T> r = new Result<>();
        r.setErrno(status);
        r.setMsg(msg);
        r.setData(data);
        r.setTimestamp(System.currentTimeMillis() / 1000L);
        r.setRequestId(UUID.randomUUID().toString().replace("-", ""));
        return r;
    }

    public static <T> Result<T> ok() {
        Result<T> r = new Result<>();
        r.setErrno(0);
        r.setMsg("success");
        r.setTimestamp(System.currentTimeMillis() / 1000L);
        r.setRequestId(UUID.randomUUID().toString().replace("-", ""));
        return r;
    }

    public static <T> Result<T> ok(T data) {
        Result<T> r = new Result<>();
        r.setErrno(0);
        r.setMsg("success");
        r.setTimestamp(System.currentTimeMillis() / 1000L);
        r.setRequestId(UUID.randomUUID().toString().replace("-", ""));
        r.setData(data);
        return r;
    }

    @ApiModelProperty(value = "返回状态码", position = 0)
    public int getErrno() {
        return errno;
    }

    @ApiModelProperty(value = "返回状态码", position = 0)
    public void setErrno(int errno) {
        this.errno = errno;
    }

    @ApiModelProperty(value = "返回信息", position = 1)
    public String getMsg() {
        return msg;
    }

    @ApiModelProperty(value = "返回信息", position = 1)
    public void setMsg(String msg) {
        this.msg = msg;
    }

    @ApiModelProperty(value = "时间戳", position = 2)
    public Long getTimestamp() {
        return timestamp;
    }

    @ApiModelProperty(value = "时间戳", position = 2)
    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @ApiModelProperty(value = "日志ID", position = 3)
    public String getRequestId() {
        return requestId;
    }

    @ApiModelProperty(value = "日志ID", position = 3)
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    @ApiModelProperty(value = "结果数据", position = 4)
    public T getData() {
        return data;
    }

    @ApiModelProperty(value = "结果数据", position = 4)
    public void setData(T data) {
        this.data = data;
    }

}