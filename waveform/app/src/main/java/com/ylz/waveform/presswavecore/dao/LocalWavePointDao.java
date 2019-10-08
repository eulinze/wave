package com.ylz.waveform.presswavecore.dao;

import com.ylz.waveform.presswavecore.model.db.LocalWavePoint;

import org.litepal.crud.DataSupport;

import java.util.List;

public class LocalWavePointDao {

    public static boolean save(LocalWavePoint wavePoint){
        return wavePoint.save();
    }
    public static List<LocalWavePoint> findByWaveId(int waveId){
        return DataSupport.where("waveId=?",String.valueOf(waveId)).find(LocalWavePoint.class);
    }

    public static int deleteAll(){
        return DataSupport.deleteAll(LocalWavePoint.class,"");
    }
    public static void saveBatch(List<LocalWavePoint> localWavePointList){
        DataSupport.saveAll(localWavePointList);
    }
}
