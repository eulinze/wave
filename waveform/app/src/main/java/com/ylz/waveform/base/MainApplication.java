package com.ylz.waveform.base;

import android.app.Application;

import com.ylz.waveform.bean.LoginBean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/8/31.
 */

public class MainApplication extends Application {

    private static MainApplication mainApplication;

    public static MainApplication getInstance() {
        return mainApplication;
    }

    public static LoginBean loginBean;
    private static BaseActivity act;

    @Override
    public void onCreate() {
        super.onCreate();
        mainApplication = this;
//        loginBean = (LoginBean) ObjectTools.getObject(LocalStorage.getInstance(this).getString("loginBean", ""));
//        loginBean = (LoginBean) SharedPreferenceUtil.getBean(this,"loginBean");
    }

    public void setAct(BaseActivity act) {
        this.act = act;
    }
}
