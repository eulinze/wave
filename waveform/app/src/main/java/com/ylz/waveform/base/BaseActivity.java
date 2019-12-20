package com.ylz.waveform.base;

import android.app.Activity;
import android.app.Dialog;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.ylz.waveform.R;
import com.ylz.waveform.tools.ActivityCollector;
import com.ylz.waveform.tools.StringUtil;

/**
 * Created by eulinze on 2018/12/19.
 */

public class BaseActivity extends AppCompatActivity {

    public Dialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    //ui线程展示长提示
    protected void showLongMessageOnUi(final Activity act, final String message) {
        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(act, message, Toast.LENGTH_LONG).show();
            }
        });
    }
    //ui线程展示短提示
    protected void showShortMessageOnUi(final Activity act, final String message) {
        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(act, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    //ui线程展示请求超时提示
    protected void showTimeOutMessageOnUi(final Activity act) {
        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(act, "数据请求超时...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void startLoadingDialog(final Activity act) {
        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (StringUtil.isNotNull(progressDialog) && progressDialog.isShowing()){
                    return;
                }
                progressDialog = new Dialog(act, R.style.progress_dialog);
                progressDialog.setContentView(R.layout.dialog);
                progressDialog.setCancelable(true);
                progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                TextView msg = (TextView) progressDialog.findViewById(R.id.id_tv_loadingmsg);
                msg.setText("玩命加载中");
                progressDialog.show();
            }
        });
    }

    public void stopLoadingDialog(Activity act) {
        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (StringUtil.isNotNull(progressDialog) && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 销毁活动时，将其从管理器中移除
        ActivityCollector.removeActivity(this);
    }
}
