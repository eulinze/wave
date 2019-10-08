package com.ylz.waveform.activity.presswave.adapter;

import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

import com.ylz.waveform.activity.presswave.enums.SearchPreciseEnum;
import com.ylz.waveform.activity.presswave.enums.TabDataSourceEnum;
import com.ylz.waveform.activity.presswave.fragment.LocalDataFragment;
import com.ylz.waveform.activity.presswave.fragment.ServerDataFragment;

public class MyDataViewPagerAdapter extends FragmentStatePagerAdapter {

    public static final int PAGER_COUNT = TabDataSourceEnum.values().length;

    private String name;
    private int searchPreciseKey;

    public MyDataViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public MyDataViewPagerAdapter(FragmentManager fm, String name, int searchPreciseKey) {
        super(fm);
        this.name = name;
        this.searchPreciseKey = searchPreciseKey;
    }

    @Override
    public Fragment getItem(int i) {
        if(i == 0){
            return LocalDataFragment.newInstance(name,searchPreciseKey);
        }else{
            return ServerDataFragment.newInstance(name,searchPreciseKey);
        }
    }

    @Override
    public int getCount() {
        return PAGER_COUNT;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String title = TabDataSourceEnum.getEnumByKey(position+1).getDesc();
        SpannableString spannableString = new SpannableString(title);
        spannableString.setSpan(new ForegroundColorSpan(Color.WHITE),0,title.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }
}
