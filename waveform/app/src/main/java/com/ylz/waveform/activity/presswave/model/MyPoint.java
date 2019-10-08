package com.ylz.waveform.activity.presswave.model;


public class MyPoint {

    private float x;

    private float y;

    private int pressUnit;

    public MyPoint(){}

    public MyPoint(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public MyPoint(float x, float y, int pressUnit) {
        this.x = x;
        this.y = y;
        this.pressUnit = pressUnit;
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
