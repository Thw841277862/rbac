package com.example.rbac.common;
/**
 * 公共状态类
 * @author thw
 * @date 2024/6/24 15:18
 */
public enum CommonStatus {
    FAIL(-1, "接口未知异常"),
    SUCCESS(200, "请求成功"),
    NOT_FOUND(404, "地址不存在"),
    MISSING_SERVLET_REQUEST_PARAMETER(400, "缺少请求参数或路径错误"),
    MISSING_PATH_VARIABLE(400, "缺少请求参数"),
    HTTP_MESSAGE_NOT_READABLE(400, "参数解析异常"),
    METHOD_ARGUMENT_NOT_VALID(400, "参数验证失败"),
    CONSTRAINT_VIOLATION(400, "参数验证失败"),
    VALIDATION(400, "参数验证失败：内部实体字段映射错误"),
    BIND(400, "参数绑定失败"),
    HTTP_REQUEST_METHOD_NOT_SUPPORTED(405, "不支持的请求方法"),
    HTTP_MEDIA_TYPE_NOT_SUPPORTED(415, "不支持的媒体类型"),
    COMPLETION(500, "CompletableFuture内部异常"),
    DECODE(30000, "数据获取异常"),
    NO_DATA(30000, "无数据"),
    PERMISSION(1, "用户无请求接口权限"),
    AUTH(1, "接口签名验证失败"),
    VALIDATE(1, "参数验证失败：内部实体字段映射错误"),
    BUSINESS(1, "接口业务内部异常"),
    BUSINESS_RUNTIME(1, "接口业务内部异常"),
    INVALID_PARAM(100, "非法的参数异常")
    ;

    private Integer busiCode;
    private String msg;

    private CommonStatus(Integer busiCode, String msg) {
        this.busiCode = busiCode;
        this.msg = msg;
    }

    public Integer getBusiCode() {
        return this.busiCode;
    }

    public void setBusiCode(Integer busiCode) {
        this.busiCode = busiCode;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
