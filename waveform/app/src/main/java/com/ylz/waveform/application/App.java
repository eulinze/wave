package com.ylz.waveform.application;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

import com.baidu.mobstat.StatService;
import com.ylz.waveform.bean.LoginBean;

import org.litepal.LitePal;

public class App extends Application{

	public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
	public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

	private static Context context;
	public static LoginBean loginBean;

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
