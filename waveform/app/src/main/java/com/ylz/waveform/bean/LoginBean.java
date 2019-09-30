package com.ylz.waveform.bean;

import java.io.Serializable;

public class LoginBean implements Serializable{
    private String id;
    private String personalCode;
    /**
     * openid : olmt4wfxS24VeeVX16_zUhZezY
     * nickname : test
     * sex : 1
     * language : zh_CN
     * city : Shenzhen
     * province : Guangdong
     * country : CN
     * headimgurl : http://wx.qlogo.cn/mmopen/ajNVdqHZLLDickRibe5D4x2ADgSfianmA4kK9hY4esrvGhmAFCe5wjox6b6pL4ibiblKnxibzVtGdqfa2UVHACfmmUsQ/0
     * privilege : []
     * unionid : o5aWQwAa7niCIXhAIRBOwglIJ7UQ
     */

    private String openid;
    private String nickname;
    private int sex;
    private String language;
    private String city;
    private String province;
    private String country;
    private String headimgurl;
    private String unionid;
    private String createTime;
    private String phoneNumber;
    private String password;
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getPersonalCode() {
        return personalCode;
    }
    public void setPersonalCode(String personalCode) {
        this.personalCode = personalCode;
    }
    public String getOpenid() {
        return openid;
    }
    public void setOpenid(String openid) {
        this.openid = openid;
    }
    public String getNickname() {
        return nickname;
    }
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    public int getSex() {
        return sex;
    }
    public void setSex(int sex) {
        this.sex = sex;
    }
    public String getLanguage() {
        return language;
    }
    public void setLanguage(String language) {
        this.language = language;
    }
    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }
    public String getProvince() {
        return province;
    }
    public void setProvince(String province) {
        this.province = province;
    }
    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }
    public String getHeadimgurl() {
        return headimgurl;
    }
    public void setHeadimgurl(String headimgurl) {
        this.headimgurl = headimgurl;
    }
    public String getUnionid() {
        return unionid;
    }
    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }
    public String getCreateTime() {
        return createTime;
    }
    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
