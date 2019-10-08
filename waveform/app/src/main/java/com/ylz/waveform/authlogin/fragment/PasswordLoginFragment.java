package com.ylz.waveform.authlogin.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ylz.waveform.R;
import com.ylz.waveform.authlogin.Config;
import com.ylz.waveform.authlogin.MyJump;
import com.ylz.waveform.authlogin.OkHttpUtil;
import com.ylz.waveform.authlogin.ResultActionEnum;
import com.ylz.waveform.authlogin.SharedPreferenceUtils;
import com.ylz.waveform.authlogin.ToastUtils;
import com.ylz.waveform.authlogin.User;

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


public class PasswordLoginFragment extends Fragment {
    private EditText phoneNumberEdit;

    private EditText passwordEdit;

    private Button btnLogin;

    private Activity activity;
    private SharedPreferenceUtils sharedPreferenceUtils;
    public static PasswordLoginFragment newInstance(){
        PasswordLoginFragment fragment = new PasswordLoginFragment();

        return fragment;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (Activity) context;
        sharedPreferenceUtils = new SharedPreferenceUtils(activity);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_password_login,container,false);
        phoneNumberEdit = view.findViewById(R.id.phone_number);
        passwordEdit = view.findViewById(R.id.password);
        btnLogin = view.findViewById(R.id.btn_login);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = phoneNumberEdit.getText().toString();
                String password = passwordEdit.getText().toString();
                Pattern pattern = Pattern.compile("^1[3|4|5|7|8][0-9]\\d{4,8}$");
                Matcher matcher = pattern.matcher(phoneNumber);
                boolean isMatch = matcher.matches();
                if(!isMatch){
                    ToastUtils.toast(activity,"请输入正确手机号码");
                }else if("".equals(password)){
                    ToastUtils.toast(activity,"请输入密码");
                }else{
                    User user = new User();
                    user.phoneNumber = phoneNumber;
                    user.password = password;
                    login(user);
                }

            }
        });
        return view;
    }
    private void login(User user){
        String url = Config.PASS_WORD_LOGIN_URL;
        Gson gson = new Gson();
        String json = gson.toJson(user);
        OkHttpUtil.post(url, json, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.toast(activity,"网络错误");
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
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtils.toast(activity,msg);
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
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                MyJump.jumpToLoginSuccess(activity);
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
