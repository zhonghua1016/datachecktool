package com.example.datacheck.dto;

/**
 * created by yuanjunjie on 2018/9/21 上午10:07
 */
public interface IResultCode {

    void setCode(int code);

    void setMessage(String message);

    int getCode();

    String getMessage();

    default String errorMsg() {
        return "["+getCode()+"]"+getMessage();
    }
}
