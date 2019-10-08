package com.ylz.waveform.activity.presswave.listener;

import com.ylz.waveform.activity.presswave.model.db.LocalWavePoint;

import java.util.List;

public interface UploadDataListener {

    void upload(List<LocalWavePoint> pointList);
}
