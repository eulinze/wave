package com.ylz.waveform.activity.presswave.model;

import com.ylz.waveform.activity.presswave.model.db.ServerWave;
import com.ylz.waveform.activity.presswave.model.db.ServerWavePoint;

import java.util.List;

public class ServerVo {

    private ServerWave serverWave;

    private List<ServerWavePoint> serverWavePointList;

    public ServerWave getServerWave() {
        return serverWave;
    }

    public void setServerWave(ServerWave serverWave) {
        this.serverWave = serverWave;
    }

    public List<ServerWavePoint> getServerWavePointList() {
        return serverWavePointList;
    }

    public void setServerWavePointList(List<ServerWavePoint> serverWavePointList) {
        this.serverWavePointList = serverWavePointList;
    }
}
