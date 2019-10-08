package com.ylz.waveform.presswavecore.listener;

import com.ylz.waveform.presswavecore.model.db.LocalWavePoint;

import java.util.List;

public interface UploadDataListener {

    void upload(List<LocalWavePoint> pointList);
}
