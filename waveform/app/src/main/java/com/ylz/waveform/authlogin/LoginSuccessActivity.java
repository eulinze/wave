package com.ylz.waveform.authlogin;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ylz.waveform.R;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LoginSuccessActivity extends AppCompatActivity {
    private SharedPreferenceUtils sharedPreferenceUtils = new SharedPreferenceUtils(this);
    private TextView userNameTv;
    private TextView phoneNumberTv ;
    private Button btnLoginOut;
    private Button btnGetUser;
    private Button btnToModifyPassword;
    private Handler handler = new Handler();
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_success);
        userNameTv = findViewById(R.id.user_name);
        phoneNumberTv = findViewById(R.id.phone_number);
        btnLoginOut = findViewById(R.id.btn_login_out);
        btnGetUser = findViewById(R.id.btn_get_user);
        btnToModifyPassword = findViewById(R.id.btn_to_modify_password);

        User user = sharedPreferenceUtils.getUser();
        init(user.userName,user.phoneNumber);


    }

    private void init(String userName, final String phoneNumber){

        userNameTv.setText(userName);
        phoneNumberTv.setText(phoneNumber);
        btnLoginOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginSuccessActivity.this);
                builder.setTitle("确定退出吗");
                builder.setPositiveButton("确定",new DialogInterface.OnClickListener(){

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String password = sharedPreferenceUtils.getPassword();
                                sharedPreferenceUtils.loginOut();
                                finish();
                                if("".equals(password)){
                                    MyJump.jumpToSetPassword(LoginSuccessActivity.this,phoneNumber);
                                }
                            }
                        });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();


            }
        });
        btnGetUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUserInfo();
            }
        });
        btnToModifyPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginSuccessActivity.this,ModifyPasswordActivity.class);
                User user = sharedPreferenceUtils.getUser();
                intent.putExtra("phoneNumber",user.phoneNumber);
                startActivity(intent);
            }
        });
    }
    private void getUserInfo(){
        String url = Config.GET_USER_URL;
        String sessionid = sharedPreferenceUtils.getSessionId();
        if(!"".equals(sessionid)){
            OkHttpUtil.get(url, sessionid,new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtils.toast(LoginSuccessActivity.this,"网络异常");
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String json = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        int action = jsonObject.getInt("action");
                        String msg = jsonObject.getString("msg");
                        if(ResultActionEnum.SUCCESS.getKey() == action){
                            Gson gson = new Gson();
                            final User user = gson.fromJson(msg,new TypeToken<User>(){}.getType());
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtils.toast(LoginSuccessActivity.this,user.userName);
                                }
                            });
                        }else{
                            sharedPreferenceUtils.loginOut();
                            MyJump.jumpToLogin(LoginSuccessActivity.this);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
        }else{
            MyJump.jumpToLogin(LoginSuccessActivity.this);
        }
    }

}
