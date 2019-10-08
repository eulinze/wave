package com.ylz.waveform.authlogin;

import android.content.Intent;
import android.os.Bundle;
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
public class ModifyPasswordActivity extends AppCompatActivity {
    private EditText oldPasswordEdit;

    private EditText newPasswordEdit;

    private Button btnModify;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_password);
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        oldPasswordEdit = findViewById(R.id.old_password);
        newPasswordEdit = findViewById(R.id.new_password);
        btnModify = findViewById(R.id.btn_modify_password);

        Intent intent = getIntent();
        if(null != intent){
            final String phoneNumber = intent.getStringExtra("phoneNumber");
            btnModify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String oldPassword = oldPasswordEdit.getText().toString();
                    String newPassword = newPasswordEdit.getText().toString();

                    if("".equals(oldPassword)){
                        ToastUtils.toast(ModifyPasswordActivity.this,"请输入原密码");
                    }else if("".equals(newPassword)){
                        ToastUtils.toast(ModifyPasswordActivity.this,"请输入新密码");
                    }else{
                        modifyPassword(phoneNumber,oldPassword,newPassword);
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

    private void modifyPassword(String phoneNumber,String oldPassword,String newPassword){
        User user = new User();
        user.phoneNumber = phoneNumber;
        user.oldPassword = oldPassword;
        user.newPassword = newPassword;
        Gson gson = new Gson();
        String json = gson.toJson(user);
        String url = Config.MODIFY_PASSWORD_URL;
        OkHttpUtil.post(url, json, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.toast(ModifyPasswordActivity.this,getResources().getString(R.string.okhttpfailed));
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
                                ToastUtils.toast(ModifyPasswordActivity.this,msg);
                            }
                        });
                    }else if(ResultActionEnum.SUCCESS.getKey() == action){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtils.toast(ModifyPasswordActivity.this,msg);
                                finish();
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
