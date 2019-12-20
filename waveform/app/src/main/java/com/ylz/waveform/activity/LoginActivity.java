package com.ylz.waveform.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.boan.LocalStorage;
import com.boan.ObjectTools;
import com.ylz.waveform.R;
import com.ylz.waveform.activity.presswave.MainActivity;
import com.ylz.waveform.application.App;
import com.ylz.waveform.base.BaseActivity;
import com.ylz.waveform.bean.LoginBean;
import com.ylz.waveform.callback.view.CommonRefreshViewCallback;
import com.ylz.waveform.httpmanager.AccountHttpManager;
import com.ylz.waveform.tools.StringUtil;
import com.ylz.waveform.tools.ToastUtils;

public class LoginActivity extends BaseActivity {


    private EditText phoneNumberEditText ;
    private EditText passwordEditText ;
    private Button enterButton ;
    private TextView goToRegist ;//go_to_regist


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        setContentView(R.layout.activity_login);

        phoneNumberEditText = (EditText) super.findViewById(R.id.phonenumber_edittext);
        passwordEditText = (EditText) super.findViewById(R.id.password_edittext);
        goToRegist  = (TextView) super.findViewById(R.id.go_to_regist );
        goToRegist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent();
                it.setClass(LoginActivity.this,RegistActivity.class);
                startActivity(it);
            }
        });

        enterButton = (Button) super.findViewById(R.id.login_button);

        enterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(StringUtil.isNull(phoneNumberEditText.getText().toString())){
                    ToastUtils.getShortToastByString(LoginActivity.this,"请输入用户名");
                    return;
                }
                if(StringUtil.isNull(passwordEditText.getText().toString())){
                    ToastUtils.getShortToastByString(LoginActivity.this,"请输入用户名");
                    return;
                }
                login();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (App.loginBean != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    private void login(){
        startLoadingDialog(LoginActivity.this);
        AccountHttpManager.getLoginData(phoneNumberEditText.getText().toString(), passwordEditText.getText().toString(), LoginBean.class, new CommonRefreshViewCallback() {
            @Override
            public void refreshView(final Object obj) {
                stopLoadingDialog(LoginActivity.this);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.getShortToastByString(LoginActivity.this, "登录成功");
                        App.loginBean = (LoginBean) obj;
                        LocalStorage.getInstance(LoginActivity.this).putString("loginBean", ObjectTools.saveObject(App.loginBean));
//                      SharedPreferenceUtil.putBean(LoginActivity.this,"loginBean", MainApplication.loginBean);
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    }
                });
            }

            @Override
            public void errorData(String errorMessage) {
                stopLoadingDialog(LoginActivity.this);
                showLongMessageOnUi(LoginActivity.this,errorMessage);
            }

            @Override
            public void getDataTimeOut() {
                stopLoadingDialog(LoginActivity.this);
                showLongMessageOnUi(LoginActivity.this,"请求超时");
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
