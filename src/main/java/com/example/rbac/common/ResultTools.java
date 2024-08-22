package com.example.rbac.common;

/**
 * 返回结果操作工具类
 * @author thw
 * @date 2024/6/24 15:19
 */
public final class ResultTools {
    /**
     * 创建成功
     *
     * @return
     */
    public static Result ofSuccess() {
        return new Result(CommonCodes.SUCCESS, "OK", null);
    }

    /**
     * 创建成功结果
     *
     * @param t 结果数据
     * @return
     */
    public static <T> Result<T> ofSuccess(T t) {
        return new Result(CommonCodes.SUCCESS, "OK", t);
    }

    /**
     * 创建失败结果
     *
     * @param msg 错误消息
     * @return
     */
    public static Result ofFailure(String msg) {
        return new Result(CommonCodes.FAILURE, msg, null);
    }


    /**
     * 创建失败结果
     *
     * @param code 错误编码
     * @param msg  错误消息
     * @return
     */
    public static Result of(int code, String msg) {
        return new Result(code, msg, null);
    }


    /**
     * 创建失败结果
     *
     * @param code 错误编码
     * @param msg  错误消息
     * @param data 结果数据
     * @return
     */
    public static <T> Result<T> of(int code, String msg, T data) {
        return new Result(code, msg, data);
    }


    private ResultTools() {
    }

}