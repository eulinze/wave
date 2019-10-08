package com.ylz.waveform.presswavecore.enums;

public enum  SearchPreciseEnum {
    FUZZY(1,"模糊搜索"),
    PRECISE(2,"精确搜索");

    SearchPreciseEnum(int key, String desc) {
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

    public static SearchPreciseEnum getEnumByKey(int key){
        for(SearchPreciseEnum searchPreciseEnum:values()){
            if(key == searchPreciseEnum.getKey()){
                return searchPreciseEnum;
            }
        }
        return null;
    }
}
