package com.ylz.waveform.bean;


public class BaseBean {
    // "errorCode":"1","data":"","key":"","errorMsg":"账号不存在或密码不正确"
    private String errorCode;
    private String errorMessage;
    private Object data;

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
