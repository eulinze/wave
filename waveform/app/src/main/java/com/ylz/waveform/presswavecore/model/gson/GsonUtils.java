package com.ylz.waveform.presswavecore.model.gson;

import com.ylz.waveform.presswavecore.model.LocalVo;
import com.ylz.waveform.presswavecore.model.ServerVo;
import com.ylz.waveform.presswavecore.model.db.LocalWave;
import com.ylz.waveform.presswavecore.model.db.LocalWavePoint;
import com.ylz.waveform.presswavecore.model.db.ServerWave;
import com.ylz.waveform.presswavecore.model.db.ServerWavePoint;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GsonUtils {

    public static ServerVo convert(ServerDataGson gson){
        ServerVo vo = new ServerVo();
        ServerDataGson.Wave wave = gson.wave;
        List<ServerDataGson.Point> pointList = gson.pointList;
        ServerWave serverWave = new ServerWave();
        serverWave.setUserName(wave.userName);
        serverWave.setUploadTime(wave.uploadTime);
        serverWave.setDownloadTime(new Date());
        serverWave.setName(wave.name);
        serverWave.setVersionName(wave.versionName);

        vo.setServerWave(serverWave);
        List<ServerWavePoint> serverWavePoints = new ArrayList<>();
        for(ServerDataGson.Point point:pointList){
            ServerWavePoint serverWavePoint = new ServerWavePoint();
            serverWavePoint.setX(point.x);
            serverWavePoint.setY(point.y);
            serverWavePoint.setPressUnit(point.pressUnit);
            serverWavePoints.add(serverWavePoint);
        }
        vo.setServerWavePointList(serverWavePoints);
        return vo;
    }

    public static UserWaveGson convert(LocalVo localVo){
        LocalWave localWave = localVo.getLocalWave();
        List<LocalWavePoint> localWavePointList = localVo.getLocalWavePointList();

        UserWaveGson userWaveVo = new UserWaveGson();
        userWaveVo.name = localWave.getName();
        userWaveVo.userName = localWave.getUserName();
        userWaveVo.uploadTime = new Date();
        userWaveVo.remark = localWave.getRemark();

        for(LocalWavePoint localWavePoint:localWavePointList){
            UserWaveGson.Point point = new UserWaveGson.Point();
            point.x = localWavePoint.getX();
            point.y = localWavePoint.getY();
            point.pressUnit = localWavePoint.getPressUnit();
            userWaveVo.pointList.add(point);
        }
        return userWaveVo;
    }
}
