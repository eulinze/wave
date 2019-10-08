package com.ylz.waveform.authlogin;

public enum OpenAuthTypeEnum {
    WEIBO_AUTH(1,"微博授权"),
    QQ_AUTH(2,"QQ授权");

    private int key;

    private String desc;

    OpenAuthTypeEnum(int key, String desc) {
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
