package com.ylz.waveform.authlogin;

import java.util.List;
import java.util.Map;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Administrator on 2019/6/4.
 */

public class OkHttpUtil {

    public static void get(String url, Callback callback){
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(callback);
    }
    public static void get(String url, String sessionid,Callback callback){
        OkHttpClient client = new OkHttpClient();

        Request request =new Request.Builder().url(url).addHeader("cookie",sessionid).build();

        client.newCall(request).enqueue(callback);
    }
    public static void post(String url, Map<String,String> data, Callback callback){
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder builder = new FormBody.Builder();
        for(String key:data.keySet()){
            builder.add(key,data.get(key));
        }
        RequestBody requestBody = builder.build();

        Request request = new Request.Builder().url(url).post(requestBody).build();

        client.newCall(request).enqueue(callback);
    }

    public static void post(String url, String json, Callback callback){
        RequestBody requestBody
                = FormBody.create(MediaType.parse("application/json; charset=utf-8"),json);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(callback);
    }

    public static String getSessionId(Response response){
        Headers headers = response.headers();
        List<String> cookies = headers.values("Set-Cookie");
        String session = cookies.get(0);
        String s = session.substring(0, session.indexOf(";"));
        return s;
    }
}
