package com.ylz.waveform.callback.view;


/**
 * Created by Administrator on 2017/8/30.
 */
public interface CommonRefreshViewCallback<T> extends NoDataCallback{

    /**
     * 刷新界面
     */
    public void refreshView(T obj);

}
