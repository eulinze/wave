package com.ylz.waveform.activity.presswave.utils;

import android.content.Context;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.ylz.waveform.activity.presswave.PressWaveApplication;

/**
 * Created by Administrator on 2019/5/18.
 */

public class ScreenUtils {

    public static Integer getScreenWidth(Context context){
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        int widthPixels = displayMetrics.widthPixels;
        int screenWidth = widthPixels;

        return screenWidth;
    }

    public static int dip2px(float dipValue){
        Context context = PressWaveApplication.getContext();
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dipValue * scale + 0.5f);
    }

    public static int px2dip(float pxValue){
        Context context = PressWaveApplication.getContext();
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(pxValue / scale + 0.5f);
    }

    public static float getTextWidth(String text,float textSize){
        if(null == text || text.length() <= 0){
            return 0;
        }
        Paint paint = new Paint();
        paint.setTextSize(textSize);
        return paint.measureText(text);
    }

    public static float getTextHeight(float textSize){
        Paint paint = new Paint();
        paint.setTextSize(textSize);
        Paint.FontMetrics fm = paint.getFontMetrics();
        return fm.descent - fm.ascent;//文本自身高度
        //return fm.bottom - fm.top + fm.leading;//文本所在行的行高
    }

    public static float getTextAscent(float textSize){
        Paint paint = new Paint();
        paint.setTextSize(textSize);
        Paint.FontMetrics fm = paint.getFontMetrics();
        return Math.abs(fm.ascent);
    }
}
