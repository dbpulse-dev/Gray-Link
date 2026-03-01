package com.gray.web.dto;


import java.io.Serializable;

/**
 * @author xieyang
 */
public class RestResult<T> implements Serializable {

    /**
     * "状态码；200：成功， 非200：失败"
     */
    protected int code;

    /**
     * 响应消息
     */
    protected String message;

    /**
     * 返回数据
     */
    protected T data;

    public void setData(T data) {
        this.data = data;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public RestResult() {
        this.code = 200;
        this.message = "success";
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public static RestResult buildSuccess() {
        return new RestResult();
    }

    public static <T> RestResult<T> buildSuccess(T data) {
        RestResult<T> restResult = new RestResult();
        restResult.data = data;
        return restResult;
    }

    public static <T> RestResult<T> buildSuccess(T data, String message) {
        RestResult<T> restResult = new RestResult();
        restResult.data = data;
        restResult.message = message;
        return restResult;
    }

    public static RestResult buildFailure() {
        RestResult restResult = new RestResult();
        restResult.code = 0;
        restResult.message = "failure";
        return restResult;
    }

    public static RestResult buildFailure(String message) {
        RestResult restResult = new RestResult();
        restResult.code = 0;
        restResult.message = message;
        return restResult;
    }

    public static RestResult buildFailure(String message, int code) {
        RestResult restResult = new RestResult();
        restResult.code = code;
        restResult.message = message;
        return restResult;
    }

}
