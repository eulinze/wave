package com.ylz.waveform.presswavecore.listener;


public interface FragmentCallback {

    void onSearchSuccess();

    void onSearchFail();

    void addObserver(SearchListener observer);
}
