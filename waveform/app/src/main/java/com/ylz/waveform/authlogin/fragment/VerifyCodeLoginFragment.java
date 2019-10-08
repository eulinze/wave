package com.ylz.waveform.authlogin.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
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
import com.ylz.waveform.authlogin.VerifyCodeTypeEnum;

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


public class VerifyCodeLoginFragment extends Fragment {
    private Button btnGetVerifyCode;
    private EditText phoneNumberEdit;
    private  EditText verifyCodeEdit;
    private Button btnLogin;
    private Activity activity;
    private SharedPreferenceUtils sharedPreferenceUtils;

    public static VerifyCodeLoginFragment newInstance(){
        VerifyCodeLoginFragment fragment = new VerifyCodeLoginFragment();

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
        View view = inflater.inflate(R.layout.fragment_verifycode_login,container,false);
        btnGetVerifyCode = view.findViewById(R.id.btn_get_verify_code);
        phoneNumberEdit = view.findViewById(R.id.phone_number);
        verifyCodeEdit = view.findViewById(R.id.verify_code);
        btnLogin = view.findViewById(R.id.btn_login);

        btnGetVerifyCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = phoneNumberEdit.getText().toString();
                Pattern pattern = Pattern.compile("^1[3|4|5|7|8][0-9]\\d{4,8}$");
                Matcher matcher = pattern.matcher(phoneNumber);
                boolean isMatch = matcher.matches();
                if(isMatch){
                    btnGetVerifyCode.setClickable(false);
                    handler.post(new CountRunnable());
                    generateVerifyCode(phoneNumber, VerifyCodeTypeEnum.LOGIN.getKey());
                }else{
                    ToastUtils.toast(activity,"请输入正确手机号");
                }
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = phoneNumberEdit.getText().toString();
                String code = verifyCodeEdit.getText().toString();
                Pattern pattern = Pattern.compile("^1[3|4|5|7|8][0-9]\\d{4,8}$");
                Matcher matcher = pattern.matcher(phoneNumber);
                boolean isMatch = matcher.matches();
                if(!isMatch){
                    ToastUtils.toast(activity,"请输入正确手机号码");
                }else if ("".equals(code)) {
                    ToastUtils.toast(activity,"请输入验证码");
                }else{
                    login(phoneNumber,code);
                }
            }
        });
        return view;
    }
    //生成验证码
    private void generateVerifyCode(String phoneNumber,int codeType){
        String url = Config.GENERATE_VERIFY_CODE_URL + "?phoneNumber="+phoneNumber+"&codeType="+codeType;
        OkHttpUtil.get(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.toast(activity,getResources().getString(R.string.okhttpfailed));
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
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtils.toast(activity,msg);
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void login(String phoneNumber,String code){
        String url = Config.VERIFY_CODE_LOGIN_URL + "?phoneNumber="+phoneNumber+"&code="+code;
        OkHttpUtil.get(url, new Callback() {
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
}
