package com.ylz.waveform.presswavecore.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.ylz.waveform.presswavecore.Config;
import com.ylz.waveform.R;
import com.ylz.waveform.presswavecore.adapter.LocalDataAdapter;
import com.ylz.waveform.presswavecore.callback.OnRefreshCallback;
import com.ylz.waveform.presswavecore.enums.SearchPreciseEnum;
import com.ylz.waveform.presswavecore.enums.TabDataSourceEnum;
import com.ylz.waveform.presswavecore.listener.FragmentCallback;
import com.ylz.waveform.presswavecore.listener.SearchListener;
import com.ylz.waveform.presswavecore.task.LocalDataTask;
import com.ylz.waveform.presswavecore.widget.BluePointViewWrap;
import com.ylz.waveform.presswavecore.widget.RecyclerViewDivider;

public class LocalDataFragment extends Fragment implements BluePointViewWrap.SaveCallback, SearchListener, OnRefreshCallback {

    private Activity activity;
    private RecyclerView recyclerView;
//    private SwipeRefreshLayout swipeRefreshLayout;
    private static FragmentCallback fragmentCallback;
    private  String name = "";


    public static LocalDataFragment newInstance(String name){
        LocalDataFragment fragment = new LocalDataFragment();
        Bundle bundle = new Bundle();
        bundle.putString("name",name);
        fragment.setArguments(bundle);
        BluePointViewWrap.setSaveCallback(fragment);
        return fragment;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = getActivity();
        fragmentCallback = (FragmentCallback) activity;
        fragmentCallback.addObserver(this);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if(null != getArguments()){
           Bundle bundle = getArguments();
           name = bundle.getString("name");
        }
        View view = inflater.inflate(R.layout.fragment_local_data,container,false);
        recyclerView = view.findViewById(R.id.local_data_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new RecyclerViewDivider(
                activity,
                LinearLayoutManager.HORIZONTAL,
                4, getResources().getColor(R.color.divide_color)));
        refresh(name,SearchPreciseEnum.FUZZY.getKey());
//        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
//        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                refresh("",SearchPreciseEnum.FUZZY.getKey());
//            }
//        });
        return view;
    }

    @Override
    public void refresh(String name,int searchPreciseKey) {
        LocalDataTask task = new LocalDataTask(name,searchPreciseKey);
        LocalDataAdapter adapter = new LocalDataAdapter(activity, Config.LOCAL_DATA_PAGE_SIZE,task);
        adapter.setOnRefreshCallback(this);
        recyclerView.setAdapter(adapter);
    }


    @Override
    public void onItemClick(TabDataSourceEnum witchTab, String text) {
        if(TabDataSourceEnum.LOCAL_DATA.equals(witchTab)){

           refresh(text,SearchPreciseEnum.PRECISE.getKey());
           fragmentCallback.onSearchSuccess();

        }
    }

    @Override
    public void onSearch(TabDataSourceEnum witchTab, String text) {
        if(TabDataSourceEnum.LOCAL_DATA.equals(witchTab)){
            refresh(text,SearchPreciseEnum.FUZZY.getKey());
            fragmentCallback.onSearchSuccess();
        }
    }

    @Override
    public void onRefreshSuccess() {
//        if(swipeRefreshLayout.isRefreshing()){
//            swipeRefreshLayout.setRefreshing(false);
//        }
    }
}
