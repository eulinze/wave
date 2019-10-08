package com.ylz.waveform.activity.presswave.model;

import com.ylz.waveform.activity.presswave.model.db.LocalWave;
import com.ylz.waveform.activity.presswave.model.db.LocalWavePoint;

import java.util.List;

public class LocalVo {

    private LocalWave localWave;

    private List<LocalWavePoint> localWavePointList;

    public LocalWave getLocalWave() {
        return localWave;
    }

    public void setLocalWave(LocalWave localWave) {
        this.localWave = localWave;
    }

    public List<LocalWavePoint> getLocalWavePointList() {
        return localWavePointList;
    }

    public void setLocalWavePointList(List<LocalWavePoint> localWavePointList) {
        this.localWavePointList = localWavePointList;
    }
}
