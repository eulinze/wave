package com.ylz.waveform.authlogin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ylz.waveform.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class BindPhoneNumberActivity extends AppCompatActivity {
    private EditText phoneNumberEditText;
    private EditText verifyCodeEditText;
    private Button btnGetVerifyCode;
    private Button btnBind;
    private SharedPreferenceUtils sharedPreferenceUtils = new SharedPreferenceUtils(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_phone_number);
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        phoneNumberEditText = findViewById(R.id.phone_number);
        verifyCodeEditText = findViewById(R.id.verify_code);
        btnGetVerifyCode = findViewById(R.id.btn_get_verify_code);
        btnBind = findViewById(R.id.btn_bind);
        Intent intent = getIntent();
        final String openUserId = intent.getStringExtra("openUserId");
        final int codeType = intent.getIntExtra("codeType",0);
        btnGetVerifyCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String phoneNumber = phoneNumberEditText.getText().toString();
                Pattern pattern = Pattern.compile("^1[3|4|5|7|8][0-9]\\d{4,8}$");
                Matcher matcher = pattern.matcher(phoneNumber);
                boolean isMatch = matcher.matches();
                if(isMatch){
                    btnGetVerifyCode.setClickable(false);
                    handler.post(new CountRunnable());
                    generateVerifyCode(phoneNumber,codeType);
                }else{
                    ToastUtils.toast(BindPhoneNumberActivity.this,"请输入正确手机号");
                }

            }
        });

        btnBind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = phoneNumberEditText.getText().toString();
                String code = verifyCodeEditText.getText().toString();
                if (!"".equals(code) && !"".equals(phoneNumber)) {
                    bind(openUserId,phoneNumber,Integer.parseInt(code),codeType);
                }
            }
        });

    }
    //生成验证码
    private void generateVerifyCode(String phoneNumber,int codeType){
        String url = Config.GENERATE_VERIFY_CODE_URL + "?phoneNumber="+phoneNumber+"&codeType="+codeType;
        OkHttpUtil.get(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.toast(BindPhoneNumberActivity.this,"网络错误");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    int action = jsonObject.getInt("action");
                    if(ResultActionEnum.FAILED.getKey() == action){
                        final String msg = jsonObject.getString("msg");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtils.toast(BindPhoneNumberActivity.this,msg);
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    //绑定电话卡
    private void bind(String openUserId,String phoneNumber,int code,int codeType){
        String url = Config.BIND_AND_LOGIN_URL + "?openUserId="+openUserId;
        VerifyCode verifyCode = new VerifyCode();
        verifyCode.setPhoneNumber(phoneNumber);
        verifyCode.setVerifyCode(code);
        verifyCode.setVerifyType(codeType);
        Gson gson = new Gson();
        String json = gson.toJson(verifyCode);
        OkHttpUtil.post(url, json, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.toast(BindPhoneNumberActivity.this,"网络错误");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    int action = jsonObject.getInt("action");
                    final String msg = jsonObject.getString("msg");
                    if(ResultActionEnum.FAILED.getKey() == action){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtils.toast(BindPhoneNumberActivity.this,msg);
                            }
                        });
                    }else if(ResultActionEnum.SUCCESS.getKey() == action){
                        Gson gson = new Gson();
                        final User user = gson.fromJson(msg,new TypeToken<User>(){}.getType());
                        String sessionid = OkHttpUtil.getSessionId(response);
                        Map<String,Object> map = new HashMap<>();
                        map.put("sessionid",sessionid);
                        map.put("userName",user.userName);
                        map.put("phoneNumber",user.phoneNumber);
                        map.put("password",user.password==null?"":user.password);
                        sharedPreferenceUtils.save(SharedPreferenceUtils.APP_SESSION_NAME,map);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                MyJump.jumpToLoginSuccess(BindPhoneNumberActivity.this);
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler();
    class CountRunnable implements Runnable{
        int count = 60;
        @Override
        public void run() {
            if(count >0){
                count--;
                btnGetVerifyCode.setText(count+"s");
                handler.postDelayed(this,1000);
            }else{
                btnGetVerifyCode.setText("重新发送验证码");
                btnGetVerifyCode.setClickable(true);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
