package com.ylz.waveform.authlogin;

public enum VerifyCodeTypeEnum {
    LOGIN(1,"登录验证码"),
    WEIBO_AUTH(2,"微博授权验证码"),
    QQ_AUTH(3,"QQ授权验证码");

    private int key;

    private String desc;

    VerifyCodeTypeEnum(int key, String desc) {
        this.key = key;
        this.desc = desc;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
