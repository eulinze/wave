package com.ylz.waveform.authlogin;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WbAuthListener;
import com.sina.weibo.sdk.auth.WbConnectErrorMessage;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.tencent.connect.UserInfo;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.ylz.waveform.R;
import com.ylz.waveform.authlogin.fragment.PasswordLoginFragment;
import com.ylz.waveform.authlogin.fragment.VerifyCodeLoginFragment;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private final static int REQUEST_CODE = 1;
    private SsoHandler mSsoHandler;
    /** 封装了 "access_token"，"expires_in"，"refresh_token"，并提供了他们的管理功能  */
    private Oauth2AccessToken mAccessToken;
    private TextView tokenTextView;

    //qq
    private Tencent mTencent;
    private IUiListener uiListener;
    private UserInfo mUserInfo;
    private String TECENT_APP_ID = "1106615656";
    private SharedPreferenceUtils sharedPreferenceUtils = new SharedPreferenceUtils(this);
    //正常登录
    private RadioGroup radioGroup;

    private final static int VERIFYCODE_LOGIN = 1;
    private final static int PASSWORD_LOGIN = 2;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
        mTencent.onActivityResultData(requestCode, resultCode, data,uiListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//        Toolbar toolbar =  findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        check();
    }


    private void init(){
        tokenTextView = findViewById(R.id.token_text_view);
        WbSdk.install(this,new AuthInfo(this, WeiboConstants.APP_KEY, WeiboConstants.REDIRECT_URL, WeiboConstants.SCOPE));
        mSsoHandler = new SsoHandler(LoginActivity.this);//SsoHandler是发起授权的核心类
        Button weiboAuthBtn = findViewById(R.id.weibo_uth_button);
        weiboAuthBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!sharedPreferenceUtils.weiboTokenIsValid()){
                    mSsoHandler.authorizeClientSso(new WeiboAuthListener());
                }else{
                    String uid = sharedPreferenceUtils.getUid();
                    authLogin(uid,OpenAuthTypeEnum.WEIBO_AUTH.getKey());
                }
            }
        });

        //qq
        mTencent = Tencent.createInstance(TECENT_APP_ID, LoginActivity.this.getApplicationContext());
        Button qqAuthBtn = findViewById(R.id.qq_auth_button);
        qqAuthBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!sharedPreferenceUtils.qqTokenIsValid(mTencent)){
                        uiListener = new QQAuthListener();
                        //all表示获取所有权限
                        mTencent.login(LoginActivity.this, "all", uiListener);
                }else{
                    String openId = sharedPreferenceUtils.getOpenId();
                    authLogin(openId,OpenAuthTypeEnum.QQ_AUTH.getKey());
                }
            }
        });

        Button btnLogin = findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String,Object> map = new HashMap<>();
                map.put("sessionid","test");
                map.put("userName","test");
                map.put("phoneNumber","13000000000");
                map.put("password","123456");
                sharedPreferenceUtils.save(SharedPreferenceUtils.APP_SESSION_NAME,map);
                MyJump.jumpToLoginSuccess(LoginActivity.this);
            }
        });

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        TabLayout.Tab tabVerycode = tabLayout.newTab().setText(getResources().getString(R.string.label_radio_verifycode));
        tabVerycode.setTag(VERIFYCODE_LOGIN);

        TabLayout.Tab tabPassword = tabLayout.newTab().setText(getResources().getString(R.string.label_radio_password));
        tabPassword.setTag(PASSWORD_LOGIN);
        tabLayout.addTab(tabVerycode);
        tabLayout.addTab(tabPassword);
        //changeLoginType(VERIFYCODE_LOGIN);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int loginType = (int) tab.getTag();
                changeLoginType(loginType);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        tabVerycode.select();
    }
    private void changeLoginType(int loginType){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if(VERIFYCODE_LOGIN == loginType){
            transaction.replace(R.id.content, VerifyCodeLoginFragment.newInstance());
            transaction.commit();
        }else if(PASSWORD_LOGIN == loginType){
            transaction.replace(R.id.content, PasswordLoginFragment.newInstance());
            transaction.commit();
        }
    }
    //微博授权回调
    private class WeiboAuthListener implements WbAuthListener {
        @Override
        public void onSuccess(final Oauth2AccessToken oauth2AccessToken) {
            LoginActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAccessToken = oauth2AccessToken;
                    if (mAccessToken.isSessionValid()) {
                        // 显示 Token
                        updateTokenView(false);
                        Toast.makeText(LoginActivity.this,"授权成功",Toast.LENGTH_SHORT).show();
                        //把微博授权信息保存到共享参数中
                        saveWeiboSharedPreference();
                        //授权登录
                        authLogin(mAccessToken.getUid(),OpenAuthTypeEnum.WEIBO_AUTH.getKey());
                        //getUserInfo(mAccessToken.getToken(),mAccessToken.getUid());
                    }
                }
            });
        }

        @Override
        public void cancel() {
            Toast.makeText(LoginActivity.this,"取消授权",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFailure(WbConnectErrorMessage wbConnectErrorMessage) {
            Toast.makeText(LoginActivity.this,"授权失败",Toast.LENGTH_SHORT).show();
        }


    }
    private void saveWeiboSharedPreference(){
        String uid = mAccessToken.getUid();
        String token = mAccessToken.getToken();
        Long expires = mAccessToken.getExpiresTime();
        Map<String,Object> map = new HashMap<>();
        map.put("uid",uid);
        map.put("token",token);
        map.put("expires",expires);
        sharedPreferenceUtils.save(OpenAuthTypeEnum.WEIBO_AUTH.toString(),map);
    }

    private void getUserInfo(String accessToken,String uid){
        String url = "https://api.weibo.com/2/users/show.json?access_token="+accessToken+"&uid="+uid;
        OkHttpUtil.get(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LoginActivity.this,"用户信息获取失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                Gson gson = new Gson();
                final UserGson userGson = gson.fromJson(json,new TypeToken<UserGson>(){}.getType());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i(TAG, "run: "+userGson.userId);
                        Toast.makeText(LoginActivity.this,userGson.userName,Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private class BaseUiListener implements IUiListener{

        @Override
        public void onComplete(Object o) {

        }

        @Override
        public void onError(UiError uiError) {

        }

        @Override
        public void onCancel() {

        }
    }
    private class QQAuthListener extends BaseUiListener {
        @Override
        public void onComplete(Object response) {
            Toast.makeText(LoginActivity.this, "授权成功", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "response:" + response);
            JSONObject obj = (JSONObject) response;
            try {
                String openID = obj.getString("openid");
                String accessToken = obj.getString("access_token");
                String expires = obj.getString("expires_in");
                //保存到共享参数中
                saveQQSharedPreference(openID,accessToken,expires);
                //授权登录
                authLogin(openID,OpenAuthTypeEnum.QQ_AUTH.getKey());
                mTencent.setOpenId(openID);
                mTencent.setAccessToken(accessToken,expires);
//                QQToken qqToken = mTencent.getQQToken();
//                mUserInfo = new UserInfo(getApplicationContext(),qqToken);
//                mUserInfo.getUserInfo(new IUiListener() {
//                    @Override
//                    public void onComplete(Object response) {
//                        Log.e(TAG,"登录成功"+response.toString());
//                    }
//                    @Override
//                    public void onError(UiError uiError) {
//                        Log.e(TAG,"登录失败"+uiError.toString());
//                    }
//                    @Override
//                    public void onCancel() {
//                        Log.e(TAG,"登录取消");
//                    }
//                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(UiError uiError) {
            Toast.makeText(LoginActivity.this, "授权失败", Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onCancel() {
            Toast.makeText(LoginActivity.this, "授权取消", Toast.LENGTH_SHORT).show();
        }
    }
    private void authLogin(String openId, final int openAuthType){
        String url = Config.AUTH_LOGIN_URL + "?openId="+openId+"&openAuthType="+openAuthType;
        OkHttpUtil.get(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.toast(LoginActivity.this,"授权登录失败");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    int action = jsonObject.getInt("action");
                    if(ResultActionEnum.SUCCESS.getKey() == action){

                        String msg = jsonObject.getString("msg");
                        Gson gson = new Gson();
                        final User user = gson.fromJson(msg,new TypeToken<User>(){}.getType());
                        Map<String,Object> map = new HashMap<>();
                        String sessionid = OkHttpUtil.getSessionId(response);
                        map.put("sessionid",sessionid);
                        map.put("userName",user.userName);
                        map.put("phoneNumber",user.phoneNumber);
                        map.put("password",user.password==null?"":user.password);
                        sharedPreferenceUtils.save(SharedPreferenceUtils.APP_SESSION_NAME,map);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                MyJump.jumpToLoginSuccess(LoginActivity.this);
                            }
                        });
                    }else if(ResultActionEnum.TO_BIND.getKey() == action){
                        String openUserId = jsonObject.getString("msg");
                        int codeType = 0;
                        if(OpenAuthTypeEnum.WEIBO_AUTH.getKey() == openAuthType){
                            codeType = VerifyCodeTypeEnum.WEIBO_AUTH.getKey();
                        }else if(OpenAuthTypeEnum.QQ_AUTH.getKey() == openAuthType){
                            codeType = VerifyCodeTypeEnum.QQ_AUTH.getKey();
                        }
                        MyJump.jumpToBindPhoneNumber(LoginActivity.this,openUserId,codeType);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void saveQQSharedPreference(String openId,String token,String expires){
        Map<String,Object> map = new HashMap<>();
        map.put("openId",openId);
        map.put("token",token);
        map.put("expires",expires);
        sharedPreferenceUtils.save(OpenAuthTypeEnum.QQ_AUTH.toString(),map);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_CODE:
                boolean denied = false;
                for(int grantResult:grantResults){
                    if(grantResult != PackageManager.PERMISSION_GRANTED) {
                        denied = true;
                        break;
                    }
                }
                if(!denied){
                    init();
                }else{
                    Toast.makeText(this,"请开启权限",Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
        }
    }

    private void check(){
        int granted = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(granted != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_CODE);
        }else{
            init();
        }
    }
    private void updateTokenView(boolean hasExisted) {
        String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(
                new java.util.Date(mAccessToken.getExpiresTime()));
        String format = getString(R.string.weibosdk_demo_token_to_string_format_1);
        tokenTextView.setText(String.format(format, mAccessToken.getToken(), date));

        String message = String.format(format, mAccessToken.getToken(), date);
        if (hasExisted) {
            message = getString(R.string.weibosdk_demo_token_has_existed) + "\n" + message;
        }
        tokenTextView.setText(message);
    }
}
