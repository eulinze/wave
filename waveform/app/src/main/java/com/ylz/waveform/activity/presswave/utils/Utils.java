package com.ylz.waveform.activity.presswave.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.view.inputmethod.InputMethodManager;

import com.ylz.waveform.activity.presswave.PressWaveApplication;

public class Utils {

    public static void toggleSoftInput(){
        InputMethodManager imm
                = (InputMethodManager) PressWaveApplication.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if(null != imm){
            //imm.hideSoftInputFromInputMethod(homeMenuView.getWindowToken(),0);
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @SuppressLint("MissingPermission")
    public static String getPhoneNumber(){
        Context context = PressWaveApplication.getContext();
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String phoneNumber = telephonyManager.getLine1Number();
        return phoneNumber;
    }
}
