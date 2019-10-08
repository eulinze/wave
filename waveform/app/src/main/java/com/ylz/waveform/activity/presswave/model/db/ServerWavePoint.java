package com.ylz.waveform.activity.presswave.model.db;

import org.litepal.crud.DataSupport;

public class ServerWavePoint extends DataSupport {

    private int id;

    private int waveId;

    private float x;

    private float y;

    private int pressUnit;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getWaveId() {
        return waveId;
    }

    public void setWaveId(int waveId) {
        this.waveId = waveId;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public int getPressUnit() {
        return pressUnit;
    }

    public void setPressUnit(int pressUnit) {
        this.pressUnit = pressUnit;
    }
}
