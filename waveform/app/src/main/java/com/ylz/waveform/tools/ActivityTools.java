package com.ylz.waveform.tools;

import android.app.Activity;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ActivityTools {
    /**
     * 栈顶activity
     */
    public  static String topActivityNmae ;

	private static Map<String,Activity> destoryMap = new HashMap<String,Activity>();

    public static String getTopActivityNmae() {
        return topActivityNmae;
    }

    public static void setTopActivityNmae(String topActivityNmae) {
        ActivityTools.topActivityNmae = topActivityNmae;
    }

    public static void removeTopActivityNmae() {
        ActivityTools.topActivityNmae = "";
    }

    public static void clearDestoryMap() {
    	destoryMap.clear();
    }
    /**
     * 添加到销毁队列
     *
     * @param activity 要销毁的activity
     */

    public static void addDestoryActivity(Activity activity, String activityName) {
    	if (destoryMap.containsKey(activityName)) {
    		destoryMap.remove(activityName);
		}
        destoryMap.put(activityName,activity);
    }
	/**
	*销毁指定Activity
	*/
    public static void destoryActivity(String activityName) {
       Set<String> keySet=destoryMap.keySet();
        for (String key:keySet){
            destoryMap.get(key).finish();
        }
    }

}
