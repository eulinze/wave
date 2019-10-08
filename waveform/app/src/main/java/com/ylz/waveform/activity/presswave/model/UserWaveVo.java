package com.ylz.waveform.activity.presswave.model;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserWaveVo {

    public String name;

    public String userName;

    public Date uploadTime;

    public String remark;

    public List<Point> pointList = new ArrayList<>();

    public static class Point{

         public float x;

         public float y;

         public int pressUnit;
    }
}
