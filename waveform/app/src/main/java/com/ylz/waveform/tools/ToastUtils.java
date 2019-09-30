package com.ylz.waveform.tools;

/**
 * Created by Administrator on 2016/10/25 0025.
 */

import android.content.Context;
import android.widget.Toast;

/****
 * 当对一个事件进行连续操作弹出Toast时，Toast会连续的显示，这种体验可能不是很恰当，
 * 应该只提示一次就行了。下面对这个问题做了处理，解决了Toast多次弹出的情况。
 */
public class ToastUtils {


    private static Context context = null;


    public static void getShortToastByString(Context context, String hint) {
        Toast.makeText(context, hint, Toast.LENGTH_SHORT).show();

    }


    public static void getLongToastByString(Context context, String hint) {
        Toast.makeText(context, hint, Toast.LENGTH_LONG).show();
    }


}
