package com.ylz.waveform.callback.httpcallback;

/**
 *
 */
public interface RequestStreamCallback {
    /**
     * 数据刷新失败
     * @param errorMessage  刷新失败提示信息
     */
    public void getDataFail(String errorMessage);

    /**
     * 数据刷新成功
     */
    public void getDataSuccess(Object obj);

    /**
     * 数据请求超时
     */
    public void getDataFailTimeOut();
}
