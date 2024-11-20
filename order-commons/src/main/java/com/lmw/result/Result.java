package com.lmw.result;

import lombok.Data;

import java.io.Serializable;

/**
 * 后端统一返回结果
 * @param <T>
 */
@Data
public class Result<T> implements Serializable {

    private Integer code; //编码：1成功，0和其它数字为失败
    private String msg; //信息
    private T data; //数据

    public static <T> Result<T> success() {
        Result<T> result = new Result<>();
        result.code = 200;
        return result;
    }
    public static <T> Result<T> success(String msg) {
            Result<T> result = new Result<>();
            result.msg = msg;
            result.code = 200;
            return result;
    }

    public static <T> Result<T> success(T object) {
        Result<T> result = new Result<>();
        result.data = object;
        result.code = 200;
        return result;
    }

    public static <T> Result<T> success(String msg, T object) {
        Result<T> result = new Result<>();
        result.msg = msg;
        result.data = object;
        result.code = 200;
        return result;
    }

    public static <T> Result<T> error(String msg) {
        Result<T> result = new Result<>();
        result.msg = msg;
        result.code = 500;
        return result;
    }

    public static <T> Result<T> error(String msg, T object) {
        Result<T> result = new Result<>();
        result.data = object;
        result.msg = msg;
        result.code = 500;
        return result;
    }

}
