package com.ylz.waveform.presswavecore.utils;

import com.ylz.waveform.presswavecore.dao.LocalWavePointDao;
import com.ylz.waveform.presswavecore.dao.ServerWavePointDao;
import com.ylz.waveform.presswavecore.model.LocalVo;
import com.ylz.waveform.presswavecore.model.ServerVo;
import com.ylz.waveform.presswavecore.model.db.LocalWave;
import com.ylz.waveform.presswavecore.model.db.LocalWavePoint;
import com.ylz.waveform.presswavecore.model.db.ServerWave;
import com.ylz.waveform.presswavecore.model.db.ServerWavePoint;

import java.util.ArrayList;
import java.util.List;

public class ConvertUtils {

    public static List<LocalVo> toLocalVoList(List<LocalWave> localWaveList){
        List<LocalVo> localVoList = new ArrayList<>();
        for(LocalWave localWave:localWaveList){
            int waveId = localWave.getId();
            List<LocalWavePoint> wavePointList = LocalWavePointDao.findByWaveId(waveId);
            LocalVo localVo = new LocalVo();
            localVo.setLocalWave(localWave);
            localVo.setLocalWavePointList(wavePointList);
            localVoList.add(localVo);
        }
        return localVoList;
    }

    public static List<ServerVo> toServerVoList(List<ServerWave> serverWaveList){
        List<ServerVo> serverVoList = new ArrayList<>();
        for(ServerWave serverWave:serverWaveList){
            int waveId = serverWave.getId();
            List<ServerWavePoint> wavePointList = ServerWavePointDao.findByWaveId(waveId);
            ServerVo serverVo = new ServerVo();
            serverVo.setServerWave(serverWave);
            serverVo.setServerWavePointList(wavePointList);
            serverVoList.add(serverVo);
        }
        return serverVoList;
    }
}
