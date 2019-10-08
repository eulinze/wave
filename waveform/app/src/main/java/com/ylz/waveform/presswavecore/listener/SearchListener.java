package com.ylz.waveform.presswavecore.listener;

import com.ylz.waveform.presswavecore.enums.TabDataSourceEnum;

public interface SearchListener {

    void onItemClick(TabDataSourceEnum witchTab, String text);

    void onSearch(TabDataSourceEnum witchTab, String text);
}
