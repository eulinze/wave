package com.ylz.waveform.presswavecore.callback;

import com.ylz.waveform.presswavecore.model.CommonData;
import com.ylz.waveform.presswavecore.model.LocalVo;

import java.util.List;

public interface OnLoadFinishCallback {

    void hasMore(List<? extends CommonData> dataList);

    void noMore(List<? extends CommonData> dataList);
}
