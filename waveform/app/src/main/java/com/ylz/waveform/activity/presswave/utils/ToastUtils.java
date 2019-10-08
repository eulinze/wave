package com.ylz.waveform.activity.presswave.utils;

import android.widget.Toast;

import com.ylz.waveform.activity.presswave.PressWaveApplication;

/**
 * Created by Administrator on 2019/5/18.
 */

public class ToastUtils {
    public static void toast(String msg){
        Toast.makeText(PressWaveApplication.getContext(),msg,Toast.LENGTH_SHORT).show();
    }
}
