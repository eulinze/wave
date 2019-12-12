package com.ylz.waveform.activity.presswave;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.ylz.waveform.R;
import com.ylz.waveform.authlogin.MyJump;
import com.ylz.waveform.authlogin.SharedPreferenceUtils;

public class SplashActivity extends AppCompatActivity {
    private SharedPreferenceUtils sharedPreferenceUtils = new SharedPreferenceUtils(this);
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        toJump();
    }

    private void toJump(){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String sessionid = sharedPreferenceUtils.getSessionId();
                if(!"".equals(sessionid)){
                    MyJump.jumpToLoginSuccess(SplashActivity.this);
                }else{
                    MyJump.jumpToLogin(SplashActivity.this);
                }
            }
        },1000);
    }
}
