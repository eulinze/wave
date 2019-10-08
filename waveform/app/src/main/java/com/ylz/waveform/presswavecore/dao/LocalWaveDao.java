package com.ylz.waveform.presswavecore.dao;

import com.ylz.waveform.presswavecore.model.Page;
import com.ylz.waveform.presswavecore.model.db.LocalWave;

import org.litepal.crud.DataSupport;

import java.util.List;

public class LocalWaveDao {

    public static List<LocalWave> findByLikeName(String name) {
        return DataSupport.where("name like ?", "%" + name + "%").find(LocalWave.class);
    }

    public static List<LocalWave> findByName(String name) {
        return DataSupport.where("name=?", name).find(LocalWave.class);
    }

    public static boolean save(LocalWave wave) {
        return wave.save();
    }

    public static int deleteAll() {
        return DataSupport.deleteAll(LocalWave.class, "");
    }

    public static List<LocalWave> findByLikePage(String name, Page page) {
        return DataSupport.where("name like ?", "%" + name + "%")
                .limit(page.getPageSize()).offset(page.getPageStart())
                .order("collectTime").find(LocalWave.class);
    }

    public static List<LocalWave> findByPage(String name, Page page) {
        return DataSupport.where("name = ?", name)
                .limit(page.getPageSize()).offset(page.getPageStart())
                .order("collectTime").find(LocalWave.class);
    }
}
