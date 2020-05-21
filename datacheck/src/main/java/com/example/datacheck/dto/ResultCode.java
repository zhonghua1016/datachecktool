package com.example.datacheck.dto;


import com.example.datacheck.constant.CommonConstants;

/**
 * created by yuanjunjie on 2018/6/4 下午9:40
 */
public enum ResultCode implements IResultCode {
    CODE_OK(CommonConstants.SUCCESS, "success"),
    CODE_FAIL(CommonConstants.FAIL, "failed"),
    TASK_STARTING(CommonConstants.STARTING, "任务进行中"),
    CODE_ERROR(CommonConstants.FAIL, "服务器错误，请联系管理员"),
    CODE_ERROR_PARAM(CommonConstants.FAIL, "参数错误"),
    CODE_ERROR_UPDATE(CommonConstants.FAIL, "更新失败"),
    CODE_ERROR_NOT_FOUND(CommonConstants.FAIL, "资源未找到"),
    CODE_ERROR_TOKEN(CommonConstants.FAIL, "Token失效，请重新登录"),
    CODE_ERROR_PRODUCT_NOT_FOUND(CommonConstants.FAIL, "商品不存在，或者已经下架"),
    CODE_ERROR_PERMISSION(CommonConstants.FAIL, "权限错误"),
    CODE_ERROR_HYSTRIX(CommonConstants.FAIL, "服务器错误，调用异常");
    private int code;
    private String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public void setCode(int code) {
        this.code = code;
    }
    @Override
    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
