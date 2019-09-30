package com.ylz.waveform.callback.view;

/**
 * Created by Administrator on 2016/9/1.
 */
public interface NoDataCallback {

    /**
     * 刷新失败提示信息
     * @param errorMessage
     */
    public void errorData(String errorMessage);

    /**
     * 数据请求超时
     */
    public void getDataTimeOut();

}
