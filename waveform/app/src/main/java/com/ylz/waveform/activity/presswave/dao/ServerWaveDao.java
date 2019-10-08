package com.ylz.waveform.activity.presswave.dao;

import com.ylz.waveform.activity.presswave.model.db.ServerWave;

import org.litepal.crud.DataSupport;

import java.util.List;

public class ServerWaveDao {

    public static List<ServerWave> findByLikeName(String name){
      return   DataSupport.where("name like ?","%"+name+"%").find(ServerWave.class);
    }
    public static List<ServerWave> findByName(String name){
        return DataSupport.where("name=?",name).find(ServerWave.class);
    }
    public static boolean save(ServerWave wave){
        return wave.save();
    }

    public static int deleteAll(){
        return DataSupport.deleteAll(ServerWave.class,"");
    }
}
