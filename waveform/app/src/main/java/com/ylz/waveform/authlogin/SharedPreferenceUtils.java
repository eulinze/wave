package com.ylz.waveform.authlogin;

import android.content.Context;
import android.content.SharedPreferences;

import com.tencent.tauth.Tencent;

import java.util.Date;
import java.util.Map;

public class SharedPreferenceUtils {
    public final static String APP_SESSION_NAME = "appSession";
    private Context context;
    private SharedPreferences sharedPreferences;
    private int PRIVATE_MODE = Context.MODE_PRIVATE;

    public SharedPreferenceUtils(Context context) {
        this.context = context;
    }

    public void save(String name, Map<String,Object> map){
        sharedPreferences = context.getSharedPreferences(name,PRIVATE_MODE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for(String key:map.keySet()){
            Object value = map.get(key);
            String valueString = String.valueOf(value);
            if(value.getClass().equals(String.class)){
                editor.putString(key,valueString);
            }else if(value.getClass().equals(Integer.class)){
                editor.putInt(key,Integer.parseInt(valueString));
            }else if(value.getClass().equals(Long.class)){
                editor.putLong(key,Long.parseLong(valueString));
            }
        }
        editor.commit();
    }

    public void clear(String name){
        sharedPreferences = context.getSharedPreferences(name,PRIVATE_MODE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }
    public boolean weiboTokenIsValid(){
        sharedPreferences = context.getSharedPreferences(OpenAuthTypeEnum.WEIBO_AUTH.toString(),PRIVATE_MODE);
        Long expires = sharedPreferences.getLong("expires",0l);
        if(expires != 0){
            if(new Date().getTime() < expires){
                return true;
            }
        }
        return false;
    }

    public boolean qqTokenIsValid(Tencent mTencent){
        sharedPreferences = context.getSharedPreferences(OpenAuthTypeEnum.QQ_AUTH.toString(),PRIVATE_MODE);
        String openId = sharedPreferences.getString("openId","");
        String token = sharedPreferences.getString("token","");
        String expires = sharedPreferences.getString("expires","");
        if(!"".equals(openId)){
            mTencent.setOpenId(openId);
            mTencent.setAccessToken(token,expires);
            return mTencent.isSessionValid();
        }
        return false;
    }

    public String getOpenId(){
        sharedPreferences = context.getSharedPreferences(OpenAuthTypeEnum.QQ_AUTH.toString(),PRIVATE_MODE);
        String openId = sharedPreferences.getString("openId","");
        return openId;
    }
    public String getUid(){
        sharedPreferences = context.getSharedPreferences(OpenAuthTypeEnum.WEIBO_AUTH.toString(),PRIVATE_MODE);
        String uid = sharedPreferences.getString("uid","");
        return uid;
    }

    public String getSessionId(){
        sharedPreferences = context.getSharedPreferences(APP_SESSION_NAME,PRIVATE_MODE);
        String sessionid = sharedPreferences.getString("sessionid","");
        return sessionid;
    }
    public String getPassword(){
        sharedPreferences = context.getSharedPreferences(APP_SESSION_NAME,PRIVATE_MODE);
        String password = sharedPreferences.getString("password","");
        return password;
    }
    public User getUser(){
        sharedPreferences = context.getSharedPreferences(APP_SESSION_NAME,PRIVATE_MODE);
        User user = new User();
        String userName = sharedPreferences.getString("userName","");
        String phoneNumber = sharedPreferences.getString("phoneNumber","");
        user.userName = userName;
        user.phoneNumber = phoneNumber;
        return user;
    }
    public void loginOut(){
        clear(OpenAuthTypeEnum.QQ_AUTH.toString());
        clear(OpenAuthTypeEnum.WEIBO_AUTH.toString());
        clear(SharedPreferenceUtils.APP_SESSION_NAME);
    }
}
