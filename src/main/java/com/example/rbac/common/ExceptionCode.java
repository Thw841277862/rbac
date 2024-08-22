package com.example.rbac.common;

import lombok.Getter;
/**
 * 异常枚举类
 * @author thw
 * @date 2024/6/24 15:18
 */
@Getter
public enum ExceptionCode {
    MISSING_PATH_VARIABLE(400, "缺少请求参数"),

    ;

    private Integer code;
    private String msg;

    private ExceptionCode(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }


}
