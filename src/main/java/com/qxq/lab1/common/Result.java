package com.qxq.lab1.common;

import lombok.AllArgsConstructor;
import lombok.Data;


import java.io.Serializable;

/**
 * @author qiu
 * @date 2020/10/31
 */
@Data
@AllArgsConstructor
public class Result<T> implements Serializable {


    private Integer code;

    private String msg;

    private T data;


    public static Result ok() {
        return new Result(1, "成功", null);
    }

    public static <T> Result ok(T t) {
        return new Result(1, "成功", t);
    }

    public static Result error() {
        return new Result(-1, "失败", null);
    }
}
