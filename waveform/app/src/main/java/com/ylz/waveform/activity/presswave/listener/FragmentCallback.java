package com.ylz.waveform.activity.presswave.listener;

import com.ylz.waveform.presswavecore.listener.SearchListener;

public interface FragmentCallback {

    void onSearchSuccess();

    void onSearchFail();

    void addObserver(SearchListener observer);
}
