package com.ylz.waveform.tools;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateTools {

    public static String getSurplusDays(Date start, int limit) {

        int days = Long.valueOf(
                (new Date().getTime() - start.getTime())
                        / (24 * 60 * 60 * 1000)).intValue();

        return "" + (days > limit ? 0 : limit - days);

    }

    public static void setPreferences(Context context, String key, String value) {
        // 获取SharedPreferences对象
        SharedPreferences sharedPre = context.getSharedPreferences("localData",
                context.MODE_PRIVATE);
        // 获取Editor对象
        SharedPreferences.Editor editor = sharedPre.edit();
        // 设置参数
        editor.putString(key, value);
        // 提交
        editor.commit();
    }

    public static String getPreferencesValue(Context context, String key) {
        SharedPreferences sharedPre = context.getSharedPreferences("localData",
                context.MODE_PRIVATE);
        String str = sharedPre.getString(key, "");
        return str;
    }

    public static void preferencesClean(Context context) {
        SharedPreferences sharedPre = context.getSharedPreferences(
                "user_config", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPre.edit();
        editor.remove("str");
        editor.remove("key");
        editor.remove("code");
        editor.remove("id");
        // editor.clear();
        editor.commit();
    }

    /**
     * 计算两个日期之间相差的天数
     *
     * @param nowDate
     * @param endDate
     * @return
     */

    public static int daysBetween(Date nowDate, Date endDate) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(nowDate);
        long nowTime = cal.getTimeInMillis();
        cal.setTime(endDate);
        long endTime = cal.getTimeInMillis();
        long between_days = (nowTime - endTime) / (1000 * 3600 * 24);

        return Integer.parseInt(String.valueOf(between_days));
    }

    public static String currentTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());
        return formatter.format(curDate);
    }

    public static String currentDay() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());
        return formatter.format(curDate);
    }
}
