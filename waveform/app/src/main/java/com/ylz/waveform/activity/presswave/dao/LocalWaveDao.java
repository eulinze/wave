package com.ylz.waveform.activity.presswave.dao;

import com.ylz.waveform.activity.presswave.model.db.LocalWave;

import org.litepal.crud.DataSupport;

import java.util.List;

public class LocalWaveDao {

    public static List<LocalWave> findByLikeName(String name){
      return   DataSupport.where("name like ?","%"+name+"%").find(LocalWave.class);
    }
    public static List<LocalWave> findByName(String name){
        return DataSupport.where("name=?",name).find(LocalWave.class);
    }
    public static boolean save(LocalWave wave){
        return wave.save();
    }

    public static int deleteAll(){
        return DataSupport.deleteAll(LocalWave.class,"");
    }
}
