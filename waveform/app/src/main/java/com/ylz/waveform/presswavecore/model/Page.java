package com.ylz.waveform.presswavecore.model;

public class Page {

    private int pageSize;

    private int pageNo;

    public Page(int pageSize, int pageNo) {
        this.pageSize = pageSize;
        this.pageNo = pageNo;
        pageStart = (pageNo-1)*pageSize;
    }

    private int pageStart;



    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageStart() {
        return pageStart;
    }

    public void setPageStart(int pageStart) {
        this.pageStart = pageStart;
    }
}
