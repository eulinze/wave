package com.ylz.waveform.presswavecore.model.gson;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class ServerDataGson {
    @SerializedName("serverWave")
    Wave wave;
    @SerializedName("serverWavePointList")
    List<Point> pointList;

    class Point{
        float x;
        float y;
        int pressUnit;
    }
    class Wave {
        String name;
        String userName;
        Date uploadTime;
        String versionName;
    }
}
