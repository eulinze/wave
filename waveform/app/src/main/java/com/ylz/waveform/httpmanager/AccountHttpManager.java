package com.ylz.waveform.httpmanager;

import android.util.Log;
import com.ylz.waveform.base.BaseHttpManager;
import com.ylz.waveform.callback.view.CommonRefreshViewCallback;
import com.ylz.waveform.tools.NetApi;
import org.json.JSONObject;

/**
 * Created by Administrator on 2018/11/21.
 */

public class AccountHttpManager extends BaseHttpManager {

    public static <T> void getLoginData(String phone, String password, final Class<T> classOfT, final CommonRefreshViewCallback commonRefreshViewCallback) {
        try {
            JSONObject object = new JSONObject();
            object.put("phone", phone);//手机号
            object.put("password", password);//密码
            commonGetData(NetApi.LOGIN_URL, commonRefreshViewCallback, false, classOfT, object.toString(), "json");
        } catch (Exception e) {
            Log.e("Exception", e.toString());
        }
    }


    public static <T> void registData(String phone, String userName, String password, final Class<T> classOfT, final CommonRefreshViewCallback commonRefreshViewCallback) {
        try {
            JSONObject object = new JSONObject();
            object.put("phone", phone);//手机号
            object.put("userName", userName);//手机号
            object.put("password", password);//密码
            object.put("ableFlag", "10700");//有效
            commonGetData(NetApi.LOGIN_URL, commonRefreshViewCallback, false, classOfT, object.toString(), "json");
        } catch (Exception e) {
            Log.e("Exception", e.toString());
        }
    }


    public static <T> void changePwd(String userMaxaccept, String oldPwd, String newPwd, String newPwdOk, final Class<T> classOfT, final CommonRefreshViewCallback commonRefreshViewCallback) {
        try {
            JSONObject object = new JSONObject();
            object.put("userMaxaccept", userMaxaccept);//用户唯一标识
            object.put("oldPwd", oldPwd);
            object.put("newPwd", newPwd);
            object.put("newPwdOk", newPwdOk);

            commonGetData(NetApi.PWD_URL, commonRefreshViewCallback, false, classOfT, object.toString(), "json");
        } catch (Exception e) {
        }
    }
}
