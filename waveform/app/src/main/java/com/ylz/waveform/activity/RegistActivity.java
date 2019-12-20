package com.ylz.waveform.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ylz.waveform.R;
import com.ylz.waveform.base.BaseActivity;
import com.ylz.waveform.bean.BaseBean;
import com.ylz.waveform.callback.httpcallback.RequestCallback;
import com.ylz.waveform.tools.NetApi;
import com.ylz.waveform.tools.StringUtil;
import com.ylz.waveform.tools.ThreadPoolTools;
import com.ylz.waveform.tools.ToastUtils;

import org.json.JSONObject;

public class RegistActivity extends BaseActivity {

    private EditText phoneNumberEditText ;
    private EditText userNameEditText ;
    private EditText passwordEditText ;
    private EditText userCertainPassEditText ;//user_certainpass_edittext
    private Button enterButton ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);

        phoneNumberEditText = (EditText) super.findViewById(R.id.phonenumber_edittext);
        userNameEditText = (EditText) super.findViewById(R.id.user_name_edittext);
        passwordEditText = (EditText) super.findViewById(R.id.password_edittext);
        userCertainPassEditText = (EditText) super.findViewById(R.id.user_certainpass_edittext);

        enterButton= (Button) super.findViewById(R.id.regist_button);
        enterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                regist();
            }
        });
    }



    private void regist(){
        if(StringUtil.isNull(phoneNumberEditText.getText().toString())){
            ToastUtils.getShortToastByString(RegistActivity.this,"请输入手机号");
            return;
        }
        if(StringUtil.isNull(userNameEditText.getText().toString())){
            ToastUtils.getShortToastByString(RegistActivity.this,"请输入密码");
            return;
        }
        if(StringUtil.isNull(passwordEditText.getText().toString())){
            ToastUtils.getShortToastByString(RegistActivity.this,"请输入密码");
            return;
        }

        if(StringUtil.isNull(userCertainPassEditText.getText().toString())){
            ToastUtils.getShortToastByString(RegistActivity.this,"请输入确认密码");
            return;
        }
        if(!StringUtil.eq(passwordEditText.getText().toString(),userCertainPassEditText.getText().toString())){
            ToastUtils.getShortToastByString(RegistActivity.this,"确认密码与密码不一致");
            return;
        }
        startLoadingDialog(RegistActivity.this);

        try {
            JSONObject object = null;
            object = new JSONObject();
            object.put("phone", phoneNumberEditText.getText().toString());//手机号
            object.put("userName", userNameEditText.getText().toString());//手机号
            object.put("password", passwordEditText.getText().toString());//密码
            object.put("ableFlag", "10700");//有效
            ThreadPoolTools.getInstance().runThread(NetApi.REGIST_URL, "json", object.toString(), new RequestCallback() {
                @Override
                public void getDataSuccess(BaseBean baseBean) {
                    if (StringUtil.eq(baseBean.getResultCode(), "0000")) {
                        stopLoadingDialog(RegistActivity.this);
                        showLongMessageOnUi(RegistActivity.this,"注册成功");
                        finish();
                    } else {
                        stopLoadingDialog(RegistActivity.this);
                        showLongMessageOnUi(RegistActivity.this,baseBean.getResultMsg());
                    }
                }
                @Override
                public void getDataFailTimeOut() {
                    stopLoadingDialog(RegistActivity.this);
                    showLongMessageOnUi(RegistActivity.this,"请求超时");
                }

                @Override
                public void getDataFail(String errorMessage) {
                    stopLoadingDialog(RegistActivity.this);
                    showLongMessageOnUi(RegistActivity.this,errorMessage);
                }
            });

        } catch (Exception e) {
            stopLoadingDialog(RegistActivity.this);
            showLongMessageOnUi(RegistActivity.this,"数据异常");
            e.printStackTrace();
        }
    }
}
