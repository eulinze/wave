package com.ylz.waveform.presswavecore.service;

import com.ylz.waveform.presswavecore.dao.ServerWaveDao;
import com.ylz.waveform.presswavecore.dao.ServerWavePointDao;
import com.ylz.waveform.presswavecore.enums.SearchPreciseEnum;
import com.ylz.waveform.presswavecore.model.ServerVo;
import com.ylz.waveform.presswavecore.model.db.ServerWave;
import com.ylz.waveform.presswavecore.model.db.ServerWavePoint;

import java.util.ArrayList;
import java.util.List;

public class ServerService {

    public static List<ServerVo> find(String name, int searchPreciseKey){
        List<ServerWave> waveList;
        if(SearchPreciseEnum.FUZZY.getKey() == searchPreciseKey){
            waveList = ServerWaveDao.findByLikeName(name);
        }else{
            waveList = ServerWaveDao.findByName(name);
        }
        List<ServerVo> serverVoList = new ArrayList<>();
        for(ServerWave serverWave:waveList){
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
