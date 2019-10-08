package com.ylz.waveform.activity.presswave.service;

import com.ylz.waveform.activity.presswave.dao.LocalWaveDao;
import com.ylz.waveform.activity.presswave.dao.LocalWavePointDao;
import com.ylz.waveform.activity.presswave.enums.SearchPreciseEnum;
import com.ylz.waveform.activity.presswave.model.LocalVo;
import com.ylz.waveform.activity.presswave.model.db.LocalWave;
import com.ylz.waveform.activity.presswave.model.db.LocalWavePoint;

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
