package com.ylz.waveform.presswavecore.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Administrator on 2019/5/18.
 */

public class ToastUtils {
    public static void toast(Context context, String msg){
        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
    }
}
