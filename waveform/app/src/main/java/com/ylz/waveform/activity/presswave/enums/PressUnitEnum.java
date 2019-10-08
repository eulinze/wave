package com.ylz.waveform.activity.presswave.enums;

public enum PressUnitEnum {
    PA(1,"帕"),
    KPA(2,"千帕"),
    BA(3,"巴"),
    MPA(4,"兆帕")
    ;


    PressUnitEnum(int key, String desc) {
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

    public static PressUnitEnum getEnumByKey(int key){
        for(PressUnitEnum pressUnitEnum:values()){
            if(pressUnitEnum.getKey() == key){
                return pressUnitEnum;
            }
        }
        return null;
    }
}
