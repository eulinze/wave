package com.ylz.waveform.application;

import android.app.Application;

public class App extends Application{

	public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
	public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

	
	public static App app;
	
	@Override
	public void onCreate() {
		app=this;
		super.onCreate();
		
	}

}
