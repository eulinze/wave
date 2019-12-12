package com.ylz.waveform.activity.presswave;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

import com.baidu.mobstat.StatService;

import org.litepal.LitePal;

public class PressWaveApplication extends Application {

    private static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        LitePal.initialize(context);
        StatService.start(this);
    }

    public static Context getContext(){
        return context;
    }

    @Override
    public void onTerminate() {
        // 程序终止的时候执行
        super.onTerminate();
    }
    @Override
    public void onLowMemory() {
        // 低内存的时候执行
        super.onLowMemory();
    }
    @Override
    public void onTrimMemory(int level) {
        // 程序在内存清理的时候执行
        super.onTrimMemory(level);
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

}
