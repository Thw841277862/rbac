package com.example.rbac.common;

import lombok.Getter;
/**
 * 基本异常类
 * @author thw
 * @date 2024/6/24 15:17
 */
@Getter
public class BaseException extends RuntimeException {
    private static final long serialVersionUID = -6357149550353160810L;

    /**
     * 错误代码.
     */
    private Integer code;

    /**
     * 错误信息.
     */
    private String msg;


//    public BaseException(ExceptionCode errorCode) {
//        super(String.valueOf(errorCode.getCode()));
//        this.code = errorCode.getCode();
//    }

    public BaseException(ExceptionCode errorCode) {
        super(String.valueOf(errorCode.getCode()));
        this.code = errorCode.getCode();
        this.msg = errorCode.getMsg();
    }

    public BaseException(String message) {
        this(CommonCodes.FAILURE, message);
    }

    public BaseException(int errorCode, String errorMsg) {
        super("错误代码： " + errorCode + ", 错误信息： " + errorMsg);
        this.code = errorCode;
        this.msg = errorMsg;
    }

    public BaseException(int errorCode, String errorMsg, Throwable cause) {
        super("错误代码： " + errorCode + ", 错误信息： " + errorMsg, cause);
        this.code = errorCode;
        this.msg = errorMsg;
    }

    public BaseException(Throwable cause) {
        super(cause.getMessage(), cause);
        this.msg = cause.getMessage();
        this.code = CommonCodes.FAILURE;
    }

}
