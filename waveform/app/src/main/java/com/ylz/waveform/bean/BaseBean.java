package com.ylz.waveform.bean;


public class BaseBean {
    // "errorCode":"1","data":"","key":"","errorMsg":"账号不存在或密码不正确"
    private String resultCode;
    private String resultMsg;
    private Object resultData;

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMsg() {
        return resultMsg;
    }

    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }

    public Object getResultData() {
        return resultData;
    }

    public void setResultData(Object resultData) {
        this.resultData = resultData;
    }
}
