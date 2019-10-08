package com.ylz.waveform.presswavecore.dao;

import com.ylz.waveform.presswavecore.model.Page;
import com.ylz.waveform.presswavecore.model.db.ServerWave;

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

    public static List<ServerWave> findByPage(String name, Page page){

        return DataSupport.where("name=?",name)
                .limit(page.getPageSize()).offset(page.getPageStart())
                .order("uploadTime").find(ServerWave.class);
    }

    public static List<ServerWave> findByLikePage(String name, Page page){

        return DataSupport.where("name like ?","%"+name+"%")
                .limit(page.getPageSize()).offset(page.getPageStart())
                .order("uploadTime").find(ServerWave.class);
    }
}
