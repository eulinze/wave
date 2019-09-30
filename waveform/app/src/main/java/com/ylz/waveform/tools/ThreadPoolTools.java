package com.ylz.waveform.tools;


import com.google.gson.Gson;
import com.ylz.waveform.bean.BaseBean;
import com.ylz.waveform.callback.httpcallback.RequestCallback;

import org.nutz.json.Json;

import java.io.File;
import java.io.InputStream;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Created by Administrator on 2017/8/29.
 */

/**
 * 请求工具类
 */
public class ThreadPoolTools {

    private static ThreadPoolTools threadPoolTools;

    ExecutorService fixedThreadPool = Executors.newFixedThreadPool(3);

    public ThreadPoolTools() {

    }

    public static ThreadPoolTools getInstance() {
        if (threadPoolTools == null) {
            synchronized (HttpTools.class) {
                if (threadPoolTools == null) {
                    return new ThreadPoolTools();
                }
            }
        }
        return threadPoolTools;
    }

    /**
     * 数据请求（子线程）
     *
     * @param url             请求路径
     * @param key             接口
     * @param json            封装后的请求数据
     * @param requestCallback 请求数据回调
     */
    public void runThread(final String url, final String key, final String json, final RequestCallback requestCallback) {
        fixedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {

                    String resultStr1 = HttpTools.getInstance().postRequest(url, key, json);
//                    BaseBean baseBean1 = Json.fromJson(BaseBean.class, resultStr1);
                    Gson gson = new Gson();
                    BaseBean baseBean = gson.fromJson(resultStr1,BaseBean.class);
                    if (StringUtil.eq(baseBean.getErrorCode(), "0")) {
                        requestCallback.getDataSuccess(baseBean);
                    } else {
                        requestCallback.getDataFail(baseBean.getErrorMessage());
                    }
                } catch (SocketTimeoutException e) {
                    e.printStackTrace();
                    requestCallback.getDataFailTimeOut();
                } catch (SocketException e) {
                    e.printStackTrace();
                    requestCallback.getDataFailTimeOut();
                } catch (Exception e) {
                    e.printStackTrace();
                    requestCallback.getDataFail(null);
                }
            }
        });
    }

    public void runThread(final String url, final String key, final String json, final File f, final RequestCallback requestCallback) {
        ThreadPoolManager.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String resultStr = HttpTools.getInstance().postFile(url, key, json, f);
                    BaseBean baseBean = GsonTools.getGson().fromJson(resultStr,BaseBean.class);
                    if (StringUtil.eq(baseBean.getErrorCode(), "0")) {
                        requestCallback.getDataSuccess(baseBean);
                    } else {
                        requestCallback.getDataFail(baseBean.getErrorMessage());
                    }
                } catch (SocketTimeoutException e) {
                    //Log.e("-------------------", "fail " + new Date().getTime() + "  " + e.getMessage());
                    e.printStackTrace();
                    requestCallback.getDataFailTimeOut();
                } catch (SocketException e) {
                    //Log.e("-------------------", "fail " + new Date().getTime() + "  " + e.getMessage());
                    e.printStackTrace();
                    requestCallback.getDataFailTimeOut();
                } catch (Exception e) {
                    //Log.e("-------------------", "fail " + new Date().getTime() + "  " + e.getMessage());
                    e.printStackTrace();
                    requestCallback.getDataFail(null);
                }
            }
        });
    }

    public  void runThread( final File f, final RequestCallback requestCallback) {
        UploadImageThreadPoolManager.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    //Log.e("postImgStart++++++++",new Date(System.currentTimeMillis()).toString());
                    boolean result = HttpTools.getInstance().postFile(f);
//                    String resultStr = HttpTools.getInstance().postFile(url, key, json, f);
                    if (result) {
                        String resultStr = "{\"code\":\"0\",\"message\":\"上传成功\",\"data\":\" \"}";
                        BaseBean baseBean = GsonTools.getGson().fromJson(resultStr,BaseBean.class);
                        requestCallback.getDataSuccess(baseBean);
                    } else {
                        String resultStr = "{\"code\":\"1\",\"message\":\"上传失败\",\"data\":\" \"}";
                        BaseBean baseBean = GsonTools.getGson().fromJson(resultStr,BaseBean.class);
                        requestCallback.getDataFail(baseBean.getErrorMessage());
                    }
                } catch (SocketTimeoutException e) {
                    e.printStackTrace();
                    //Log.e("ImgTimeout++++++++",e.toString());
                    requestCallback.getDataFailTimeOut();
                } catch (SocketException e) {
                    e.printStackTrace();
                    //Log.e("ImgSocketExcep++++++++",e.toString());
                    requestCallback.getDataFailTimeOut();
                } catch (Exception e) {
                    e.printStackTrace();
                    //Log.e("ImgException++++++++",e.toString());
                    String resultStr = "{\"code\":\"1\",\"message\":\"上传失败\",\"data\":\" \"}";
//                    //Log.e("postImgEnd++++++++",new Date(System.currentTimeMillis()).toString());
                    BaseBean baseBean = GsonTools.getGson().fromJson(resultStr,BaseBean.class);
                    requestCallback.getDataFail(baseBean.getErrorMessage());
                }
            }
        });
    }

    public void runThreadStream(final String url, final String key, final String json, final RequestCallback callback) {
        ThreadPoolManager.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                try {
//                    Log.e("postStart++++++++",new Date(System.currentTimeMillis()).toString());
                    InputStream in = HttpTools.getInstance().postRequestStream(url, key, json);
                    String jsonStr = StringUtil.inputStreamToStr(in);
//                    Log.e("postEnd++++++++",new Date(System.currentTimeMillis()).toString());
                    BaseBean baseBean = GsonTools.getGson().fromJson(jsonStr,BaseBean.class);
                    //Log.e("runThreadStream", "runThreadStream--success"+jsonStr);
                    if (StringUtil.eq(baseBean.getErrorCode(), "0")) {
                        callback.getDataSuccess(baseBean);
                    } else {
                        callback.getDataFail(baseBean.getErrorMessage());
                    }
                } catch (SocketTimeoutException e) {
                    e.printStackTrace();
//                    Log.e("SocketTimeout++++++++",e.toString());
                    ////Log.e("runThreadStream", "runThreadStream--SocketTimeoutException");
                    callback.getDataFailTimeOut();
                } catch (SocketException e) {
                    e.printStackTrace();
//                    Log.e("SocketException++++++++",e.toString());
                    callback.getDataFailTimeOut();
                } catch (Exception e) {
                    e.printStackTrace();
//                    Log.e("Exception++++++++",e.toString());
                    ////Log.e("runThreadStream", "runThreadStream--"+e.toString());
                    callback.getDataFail("网络可能不稳定，请重试");
//                    callback.getDataFail(e.toString());
                }
            }
        });
    }
}
