package com.ylz.waveform.base;


import com.google.gson.Gson;
import com.ylz.waveform.bean.BaseBean;
import com.ylz.waveform.callback.httpcallback.RequestCallback;
import com.ylz.waveform.callback.view.CommonRefreshViewCallback;
import com.ylz.waveform.tools.GsonTools;
import com.ylz.waveform.tools.StringUtil;
import com.ylz.waveform.tools.ThreadPoolTools;

import java.io.File;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;


/**
 * Created by Administrator on 2018/11/21.
 */

public  class BaseHttpManager {


    public static <T> void commonGetData(String url, final CommonRefreshViewCallback commonRefreshViewCallback, final boolean isList, final Class<T> classOfT, String str, String key) {
        try {
            ThreadPoolTools.getInstance().runThread(url, key, str, new RequestCallback() {
                @Override
                public void getDataSuccess(BaseBean baseBean) {
                    if (isList){
                        commonRefreshViewCallback.refreshView(parseString2List(baseBean.getResultData().toString(), classOfT));
                    }else{
                        String myJson=   GsonTools.getGson().toJson(baseBean.getResultData());//将gson转化为json
                        commonRefreshViewCallback.refreshView(GsonTools.getGson().fromJson(myJson, GsonTools.wrap(classOfT)));
                    }
                }
                @Override
                public void getDataFailTimeOut() {
                    commonRefreshViewCallback.getDataTimeOut();
                }

                @Override
                public void getDataFail(String errorMessage) {
                    if (StringUtil.isNull(errorMessage)){
                        commonRefreshViewCallback.errorData("获取数据失败");
                    }else{
                        commonRefreshViewCallback.errorData(errorMessage);
                    }

                }
            });

        } catch (Exception e) {
            commonRefreshViewCallback.errorData("网络可能不稳定，请重试");
            e.printStackTrace();
        }
    }

    public static <T> void commonGetData(String url, String key, String str, File f, final CommonRefreshViewCallback commonRefreshViewCallback, final Class<T> classOfT) {
        try {
            ThreadPoolTools.getInstance().runThread(url, key, str, f, new RequestCallback() {
                @Override
                public void getDataSuccess(BaseBean baseBean) {
                        commonRefreshViewCallback.refreshView(GsonTools.getGson().fromJson(baseBean.getResultData().toString(), GsonTools.wrap(classOfT)));
                }
                @Override
                public void getDataFailTimeOut() {
                    commonRefreshViewCallback.getDataTimeOut();
                }

                @Override
                public void getDataFail(String errorMessage) {
                    commonRefreshViewCallback.errorData(errorMessage);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <T> void commonUpLoadFtpImage(File f, final CommonRefreshViewCallback commonRefreshViewCallback, final Class<T> classOfT) {
        try {
            ThreadPoolTools.getInstance().runThread( f, new RequestCallback() {
                @Override
                public void getDataSuccess(BaseBean baseBean) {
                    commonRefreshViewCallback.refreshView(GsonTools.getGson().fromJson(baseBean.getResultData().toString(), GsonTools.wrap(classOfT)));
                }
                @Override
                public void getDataFailTimeOut() {
                    commonRefreshViewCallback.getDataTimeOut();
                }

                @Override
                public void getDataFail(String errorMessage) {
                    commonRefreshViewCallback.errorData(errorMessage);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <T> void commonGetData(String url, String key, String str, final boolean isList, final Class<T> classOfT, final CommonRefreshViewCallback commonRefreshViewCallback) {
        try {
            ThreadPoolTools.getInstance().runThreadStream(url, key, str, new RequestCallback() {
                @Override
                public void getDataSuccess(BaseBean baseBean) {
                    if (isList){
                        commonRefreshViewCallback.refreshView(parseString2List(baseBean.getResultData().toString(), classOfT));
                    }else {
                        commonRefreshViewCallback.refreshView(GsonTools.getGson().fromJson(baseBean.getResultData().toString(), GsonTools.wrap(classOfT)));
                    }
                }
                @Override
                public void getDataFailTimeOut() {
                    commonRefreshViewCallback.getDataTimeOut();
                }

                @Override
                public void getDataFail(String errorMessage) {
                    commonRefreshViewCallback.errorData(errorMessage);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public   static <T> List<T> parseString2List(String json, Class clazz) {
        Type type = new ParameterizedTypeImpl(clazz);
        List<T> list =  new Gson().fromJson(json, type);
        return list;
    }

    private static  class ParameterizedTypeImpl implements ParameterizedType {
        Class clazz;

        public ParameterizedTypeImpl(Class clz) {
            clazz = clz;
        }

        @Override
        public Type[] getActualTypeArguments() {
            return new Type[]{clazz};
        }

        @Override
        public Type getRawType() {
            return List.class;
        }

        @Override
        public Type getOwnerType() {
            return null;
        }
    }

}
