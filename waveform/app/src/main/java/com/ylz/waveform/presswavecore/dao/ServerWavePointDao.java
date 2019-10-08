package com.ylz.waveform.presswavecore.dao;

import com.ylz.waveform.presswavecore.model.db.ServerWavePoint;

import org.litepal.crud.DataSupport;

import java.util.List;

public class ServerWavePointDao {

    public static boolean save(ServerWavePoint wavePoint){
        return wavePoint.save();
    }
    public static List<ServerWavePoint> findByWaveId(int waveId){
        return DataSupport.where("waveId=?",String.valueOf(waveId)).find(ServerWavePoint.class);
    }

    public static int deleteAll(){
        return DataSupport.deleteAll(ServerWavePoint.class,"");
    }

    public static void saveBatch(List<ServerWavePoint> serverWavePoints){
        DataSupport.saveAll(serverWavePoints);
    }
}
