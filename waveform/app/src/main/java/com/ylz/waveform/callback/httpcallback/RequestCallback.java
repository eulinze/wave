package com.ylz.waveform.callback.httpcallback;


import com.ylz.waveform.bean.BaseBean;

/**
 *
 */
public interface RequestCallback {
    /**
     * 数据刷新失败
     * @param errorMessage  刷新失败提示信息
     */
    public void getDataFail(String errorMessage);

    /**
     * 数据刷新成功
     * @param baseBean
     */
    public void getDataSuccess(BaseBean baseBean);

    /**
     * 数据请求超时
     */
    public void getDataFailTimeOut();
}
