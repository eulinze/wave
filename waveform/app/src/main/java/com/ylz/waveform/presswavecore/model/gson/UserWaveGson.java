package com.ylz.waveform.presswavecore.model.gson;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserWaveGson {

    public String name;

    public String userName;

    public Date uploadTime;

    public String remark;

    public String id;

    public List<Point> pointList = new ArrayList<>();

    public static class Point{

         public float x;

         public float y;

         public int pressUnit;
    }
}
