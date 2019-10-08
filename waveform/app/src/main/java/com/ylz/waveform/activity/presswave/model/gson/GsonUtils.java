package com.ylz.waveform.activity.presswave.model.gson;

import com.ylz.waveform.activity.presswave.dao.ServerWaveDao;
import com.ylz.waveform.activity.presswave.model.LocalVo;
import com.ylz.waveform.activity.presswave.model.ServerVo;
import com.ylz.waveform.activity.presswave.model.UserWaveVo;
import com.ylz.waveform.activity.presswave.model.db.LocalWave;
import com.ylz.waveform.activity.presswave.model.db.LocalWavePoint;
import com.ylz.waveform.activity.presswave.model.db.ServerWave;
import com.ylz.waveform.activity.presswave.model.db.ServerWavePoint;

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

    public static UserWaveVo convert(LocalVo localVo){
        LocalWave localWave = localVo.getLocalWave();
        List<LocalWavePoint> localWavePointList = localVo.getLocalWavePointList();

        UserWaveVo userWaveVo = new UserWaveVo();
        userWaveVo.name = localWave.getName();
        userWaveVo.userName = localWave.getUserName();
        userWaveVo.uploadTime = new Date();
        userWaveVo.remark = localWave.getRemark();

        for(LocalWavePoint localWavePoint:localWavePointList){
            UserWaveVo.Point point = new UserWaveVo.Point();
            point.x = localWavePoint.getX();
            point.y = localWavePoint.getY();
            point.pressUnit = localWavePoint.getPressUnit();
            userWaveVo.pointList.add(point);
        }
        return userWaveVo;
    }
}
