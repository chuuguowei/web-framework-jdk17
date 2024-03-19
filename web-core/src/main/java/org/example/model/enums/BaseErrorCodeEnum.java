package org.example.model.enums;

public enum BaseErrorCodeEnum {

    OK(0, "success", "INFO"),

    // 用户端校验异常
    REQUEST_FAIL(400, "request error", "ERROR"),
    ACCESS_PARAMS_ERROR(40000, "用户访问参数错误", "ERROR"),
    ACCESS_PARAMS_INCOMPLETE(40001, "必填参数为空", "ERROR"),
    ACCESS_PARAMS_JSON_INVALID(40002, "参数非法-JSON数据转换异常", "ERROR"),
    ACCESS_PARAMS_RANGE_EXCEED(40003, "参数值超出允许范围", "ERROR"),
    ACCESS_PARAMS_BATCH_EXCEED(40004, "参数数量超出允许范围", "ERROR"),
    ACCESS_CONTENT_TYPE_ERROR(40005, "Content-Type不支持", "ERROR"),
    ACCESS_FILE_ERROR(40006, "用户上传文件异常", "ERROR"),
    ACCESS_FILE_TYPE_INVALID(40007, "上传文件类型无效", "ERROR"),
    ACCESS_FILE_SIZE_EXCEED(40008, "上传文件大小超出允许范围", "ERROR"),

    ACCESS_AUTH_ERROR(401, "用户访问权限异常", "ERROR"),
    ACCESS_SIGN_INVALID(40100, "用户访问签名无效", "ERROR"),

    ACCESS_REQUEST_METHOD_ERROR(405, "HTTP请求方法不支持", "ERROR"),

    SYSTEM_ERROR(500, "server error", "ERROR"),
    // 内部服务异常
    INTERNAL_SQL_ERROR(50000, "对不起，服务器出错了，请稍候再试", "ERROR"),
    INTERNAL_IO_ERROR(50001, "对不起，服务器出错了，请稍候再试", "ERROR"),

    // 三方依赖服务异常
    STARLIGHT_RPC_ERROR(501, "RPC接口请求异常", "ERROR"),
    EXTERNAL_SERVICE_ERROR(502, "对不起，服务器出错了，请稍候再试", "ERROR"),
    EXTERNAL_RAL_ERROR(50201, "对不起，服务器出错了，请稍候再试", "ERROR"),
    EXTERNAL_BP_ERROR(50202, "对不起，服务器出错了，请稍候再试", "ERROR"),
    EXTERNAL_CONNECT_ERROR(50203, "对不起，服务器出错了，请稍后再试", "ERROR"),
    EXTERNAL_ES_ERROR(50204, "对不起，服务器出错了，请稍候再试", "ERROR"),

    EXTERNAL_TIMEOUT_ERROR(503, "对不起，服务器出错了，请稍后再试", "ERROR"),
    EXTERNAL_SOCKET_TIMEOUT_ERROR(50300, "对不起，服务器出错了，请稍后再试", "ERROR");

    private Integer code;
    private String msg;
    private String errorLevel;

    BaseErrorCodeEnum(Integer code, String msg, String errorLevel) {
        this.code = code;
        this.msg = msg;
        this.errorLevel = errorLevel;
    }

    public static String getMsgByCode(Integer code) {
        BaseErrorCodeEnum[] values = BaseErrorCodeEnum.values();
        for (BaseErrorCodeEnum ec : values) {
            if (ec.code.equals(code)) {
                return ec.msg;
            }
        }
        return "";
    }

    public static String getErrorLevelByCode(int code) {
        BaseErrorCodeEnum[] values = BaseErrorCodeEnum.values();
        for (BaseErrorCodeEnum ec : values) {
            if (ec.code.equals(code)) {
                return ec.errorLevel;
            }
        }
        return "";
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public BaseErrorCodeEnum setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public String getErrorLevel() {
        return errorLevel;
    }

    public void setErrorLevel(String errorLevel) {
        this.errorLevel = errorLevel;
    }
}
