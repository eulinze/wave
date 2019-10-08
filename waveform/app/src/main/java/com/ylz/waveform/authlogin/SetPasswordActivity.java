package com.ylz.waveform.authlogin;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import com.ylz.waveform.R;
public class SetPasswordActivity extends AppCompatActivity {

    private EditText passwordEdit;

    private Button btnSetPassword;

    private Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_password);
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        passwordEdit = findViewById(R.id.password);
        btnSetPassword = findViewById(R.id.btn_set_password);
        Intent intent = getIntent();
        if(null != intent){
            final String phoneNumber = intent.getStringExtra("phoneNumber");

            btnSetPassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String password = passwordEdit.getText().toString();
                    if(!"".equals(password)){
                        setPassword(phoneNumber,password);
                    }else{
                        ToastUtils.toast(SetPasswordActivity.this,"请输入密码");
                    }

                }
            });
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

    private void setPassword(String phoneNumber,String password){
        String url = Config.SET_PASSWORD_URL;
        User user = new User();
        user.phoneNumber = phoneNumber;
        user.password = password;
        Gson gson = new Gson();
        String json = gson.toJson(user);

        OkHttpUtil.post(url, json, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.toast(SetPasswordActivity.this,getResources().getString(R.string.okhttpfailed));
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
                                ToastUtils.toast(SetPasswordActivity.this,msg);
                            }
                        });
                    }else if(ResultActionEnum.SUCCESS.getKey() == action){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtils.toast(SetPasswordActivity.this,msg);
                                AlertDialog.Builder builder = new AlertDialog.Builder(SetPasswordActivity.this);
                                builder.setTitle("提示");
                                builder.setMessage("密码修改成功！");
                                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                });
                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
