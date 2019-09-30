package com.ylz.waveform.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import com.ylz.waveform.R;
import com.ylz.waveform.tools.XMLSerivce;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class TestUpdateActivity extends Activity {

    private List beanList;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pullParseXml();
    }

    private void pullParseXml(){

        final Message message = new Message();
        beanList = new ArrayList<>();

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    URL url = new URL("http://bbs.csdn.net/recommend_tech_topics.atom");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(5000);
                    int code = conn.getResponseCode();
                    if (code == 200){
                        Log.e("XML","请求成功");
                        InputStream is = conn.getInputStream();
                        beanList = XMLSerivce.getNewsInfo(is);
                        Log.e("XML",beanList.size()+ "");

                        // 成功获取数据 给主线程发消息
                        message.what = 3;
                        handler.sendMessage(message);
                    }

                } catch (Exception e) {
                    // 获取数据失败，给主线程发消息，处理数据
                    message.what = 4;
                    handler.sendMessage(message);

                    e.printStackTrace();
                }

            }
        }).start();

    }

    //接收线程发送过来信息，并用TextView追加显示
    public Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.e("22222222222",msg.what+"");
        }
    };

}



