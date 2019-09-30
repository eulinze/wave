package com.ylz.waveform.tools;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @author yulz
 * @ClassName: HttpTools
 * @Description: TODO(所有数据获取的功用HTTP请求类)
 */
public class HttpTools {

    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");

    private final String TAG = HttpTools.class.getSimpleName();

    private static HttpTools manager;

    public static HttpTools getInstance() {
        if (manager == null) {
            synchronized (HttpTools.class) {
                if (manager == null) {
                    return new HttpTools();
                }
            }
        }
        return manager;
    }

    /**
     * get方式Http请求
     *
     * @param url
     * @param mJsonObject
     * @return
     * @throws Exception
     * @throws IOException
     */
    public String getRequest(String url, Map<String, String> mJsonObject)
            throws Exception, IOException {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            return response.body().string();
        } else {
            throw new IOException("Unexpected code " + response);
        }
    }

    /**
     * post方式Http请求
     *
     * @param url
     * @param json
     * @return
     * @throws IOException
     */
    public String postRequest(String url, String key, String json) throws IOException {
        //MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();

        FormBody formBody = new FormBody.Builder()
                .add(key, json)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            return response.body().string();
        } else {
            return null;
        }
    }

    public synchronized String postFile(String url, String key, String json, File f) throws IOException {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();
        // mImgUrls为存放图片的url集合
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);

        if (f != null) {
            builder.addFormDataPart("f", f.getName(), RequestBody.create(MEDIA_TYPE_PNG, f));
        }
        //添加其它信息
        builder.addFormDataPart(key, json);


        MultipartBody requestBody = builder.build();
        //构建请求
        Request request = new Request.Builder()
                .url(url)//地址
                .post(requestBody)//添加请求体
                .build();
        //Log.e("-------------------", "start " + new Date().getTime());
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            //Log.e("-------------------", "success " + new Date().getTime());
            return response.body().string();
        } else {
            return null;
        }
    }

    public synchronized boolean postFile( File f) throws IOException {
        // TODO 可以首先去判断一下网络
        FTPClientFunctions ftpClient = new FTPClientFunctions();
        boolean connectResult = ftpClient.ftpConnect(NetApi.FTP_URL, NetApi.FTP_USER, NetApi.FTP_PASSWORD, NetApi.FTP_PORT);
        if (connectResult) {
            String saveFileDirectory = "";
             saveFileDirectory =NetApi.SECURITY_IMAGE_FILE_DIRECTORY;
            boolean changeDirResult = ftpClient.ftpChangeDir(saveFileDirectory);
            //Log.e("changeDirResult",saveFileDirectory );
            if (!changeDirResult){
                changeDirResult = ftpClient.createPath(saveFileDirectory);
            }
            if (changeDirResult) {
                boolean uploadResult = ftpClient.ftpUpload(f.getAbsolutePath(), f.getName(), "");
                //Log.e("changeDirResult",f.getAbsolutePath() );
                //Log.e("changeDirResult",f.getName() );
                if (uploadResult) {
                    Log.w(TAG, "上传成功");
                    boolean disConnectResult = ftpClient.ftpDisconnect();

                    if(disConnectResult) {
                        //Log.e(TAG, "关闭ftp连接成功");
                        return disConnectResult;
                    } else {
                        //Log.e(TAG, "关闭ftp连接失败");
                        return disConnectResult;
                    }
                } else {
                    Log.w(TAG, "上传失败");
                    return false;
                }
            } else {
                Log.w(TAG, "切换ftp目录失败");
                return false;
            }

        } else {
            Log.w(TAG, "连接ftp服务器失败");
            return false;
        }
    }

    public InputStream postRequestStream(String url, String key, String json) throws IOException {
        //MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();

        FormBody formBody = new FormBody.Builder()
                .add(key, json)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();
        Response response = client.newCall(request).execute();
        //Log.e("postRequestStream", response.toString());
        if (response.isSuccessful()) {
            return response.body().byteStream();
        } else {
            return null;
        }
    }
}
