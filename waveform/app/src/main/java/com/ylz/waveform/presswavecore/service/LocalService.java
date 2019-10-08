package com.ylz.waveform.presswavecore.service;

import com.ylz.waveform.presswavecore.dao.LocalWaveDao;
import com.ylz.waveform.presswavecore.dao.LocalWavePointDao;
import com.ylz.waveform.presswavecore.enums.SearchPreciseEnum;
import com.ylz.waveform.presswavecore.model.LocalVo;
import com.ylz.waveform.presswavecore.model.db.LocalWave;
import com.ylz.waveform.presswavecore.model.db.LocalWavePoint;

import java.util.ArrayList;
import java.util.List;

public class LocalService {

    public static List<LocalVo> find(String name, int searchPreciseKey){
        List<LocalWave> waveList;
        if(SearchPreciseEnum.FUZZY.getKey() == searchPreciseKey){
            waveList = LocalWaveDao.findByLikeName(name);
        }else{
            waveList = LocalWaveDao.findByName(name);
        }

        List<LocalVo> localVoList = new ArrayList<>();
        for(LocalWave localWave:waveList){
            int waveId = localWave.getId();
            List<LocalWavePoint> wavePointList = LocalWavePointDao.findByWaveId(waveId);
            LocalVo localVo = new LocalVo();
            localVo.setLocalWave(localWave);
            localVo.setLocalWavePointList(wavePointList);
            localVoList.add(localVo);
        }
        return localVoList;
    }
}
