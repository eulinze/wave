package com.ylz.waveform.authlogin;

import android.app.Activity;
import android.content.Intent;

import com.ylz.waveform.activity.LoginActivity;

public class MyJump {
    public final static String ACTION_LOGIN_SUCCESS = "com.ylz.waveform.login.loginSuccess";
    public static void jumpToLogin(Activity activity){
        Intent intent = new Intent(activity, LoginActivity.class);
        activity.finish();
        activity.startActivity(intent);
    }
    public static void jumpToBindPhoneNumber(Activity activity,String openUserId,int codeType){
        Intent intent = new Intent(activity, BindPhoneNumberActivity.class);
        intent.putExtra("openUserId",openUserId);
        intent.putExtra("codeType",codeType);
        activity.finish();
        activity.startActivity(intent);
    }
    public static void jumpToLoginSuccess(Activity activity){
        Intent intent = new Intent();
        intent.setAction(ACTION_LOGIN_SUCCESS);
        activity.finish();
        activity.startActivity(intent);
    }

    public static void jumpToSetPassword(Activity activity,String phoneNumber){
        Intent intent = new Intent(activity, SetPasswordActivity.class);
        intent.putExtra("phoneNumber",phoneNumber);
        activity.startActivity(intent);
    }

    public static void jumpToModifyPassword(Activity activity,String phoneNumber){
        Intent intent = new Intent(activity, ModifyPasswordActivity.class);
        intent.putExtra("phoneNumber",phoneNumber);
        activity.startActivity(intent);
    }
}
