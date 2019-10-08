package com.ylz.waveform.presswavecore.enums;

public enum TabDataSourceEnum {
    LOCAL_DATA(1,"本地数据"),
    SERVICE_DATA(2,"服务器数据");

    TabDataSourceEnum(int key, String desc) {
        this.key = key;
        this.desc = desc;
    }

    private int key;

    private String desc;

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

    public static TabDataSourceEnum getEnumByKey(int key){
        for(TabDataSourceEnum tabDataSourceEnum:values()){
            if(key == tabDataSourceEnum.getKey()){
                return tabDataSourceEnum;
            }
        }
        return null;
    }
}
