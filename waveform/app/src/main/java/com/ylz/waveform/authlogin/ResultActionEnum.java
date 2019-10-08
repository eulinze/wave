package com.ylz.waveform.authlogin;

public enum ResultActionEnum {
    SUCCESS(1,"成功"),
    FAILED(2,"失败"),
    TO_BIND(3,"绑定电话号");

    private int key;

    private String desc;

    ResultActionEnum(int key, String desc) {
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
