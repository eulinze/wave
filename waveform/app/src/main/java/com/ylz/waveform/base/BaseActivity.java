package com.ylz.waveform.base;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.widget.Toast;

/**
 * Created by eulinze on 2018/12/19.
 */

public class BaseActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    protected  void showLongMessageOnUi(final Activity act , final String message){
        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(act, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    protected  void showShortMessageOnUi(final Activity act , final String message){
        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(act, message, Toast.LENGTH_SHORT).show();
            }
        });
    }


    protected  void showTimeOutMessageOnUi(final Activity act ){
        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(act, "数据请求超时...", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
