package com.ylz.waveform.activity.presswave.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ylz.waveform.R;
import com.ylz.waveform.activity.presswave.enums.SearchPreciseEnum;
import com.ylz.waveform.activity.presswave.listener.FragmentCallback;
import com.ylz.waveform.activity.presswave.model.LocalVo;
import com.ylz.waveform.activity.presswave.service.LocalService;
import com.ylz.waveform.activity.presswave.widget.BluePointViewWrap;
import com.ylz.waveform.activity.presswave.widget.LocalPointViewWrap;
import com.ylz.waveform.activity.presswave.widget.PointView;
import com.ylz.waveform.activity.presswave.widget.RecyclerViewDivider;
import com.ylz.waveform.presswavecore.enums.TabDataSourceEnum;
import com.ylz.waveform.presswavecore.listener.SearchListener;

import java.util.List;

public class LocalDataFragment extends Fragment implements BluePointViewWrap.SaveCallback, SearchListener {

    private Activity activity;
    private RecyclerView recyclerView;
    private static FragmentCallback fragmentCallback;


    public static LocalDataFragment newInstance(String name,int searchPreciseKey){
        LocalDataFragment fragment = new LocalDataFragment();
        Bundle bundle = new Bundle();
        bundle.putString("name",name);
        bundle.putInt("searchPreciseKey",searchPreciseKey);
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
        String name = "";
        int searchPreciseKey = 0;
        if(null != getArguments()){
           Bundle bundle = getArguments();
           name = bundle.getString("name");
            searchPreciseKey = bundle.getInt("searchPreciseKey");
        }
        View view = inflater.inflate(R.layout.fragment_local_data,container,false);
        recyclerView = view.findViewById(R.id.local_data_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new RecyclerViewDivider(
                activity,
                LinearLayoutManager.HORIZONTAL,
                4, getResources().getColor(R.color.divide_color)));
        new LocalDataLoadTask().execute(name,searchPreciseKey);
        return view;
    }

    @Override
    public void refresh() {
        new LocalDataLoadTask().execute("", SearchPreciseEnum.FUZZY.getKey());
    }


    @Override
    public void onItemClick(TabDataSourceEnum witchTab, String text) {
        if(TabDataSourceEnum.LOCAL_DATA.equals(witchTab)){
            new SearchTask(text,SearchPreciseEnum.PRECISE.getKey()).execute();
        }
    }

    @Override
    public void onSearch(TabDataSourceEnum witchTab, String text) {
        if(TabDataSourceEnum.LOCAL_DATA.equals(witchTab)){
            new SearchTask(text,SearchPreciseEnum.FUZZY.getKey()).execute();
        }
    }

    class LocalDataLoadTask extends AsyncTask<Object,Integer,List<LocalVo>>{

        @Override
        protected List<LocalVo> doInBackground(Object... objects) {
            String name = (String) objects[0];
            int searchPreciseKey = (int) objects[1];
            List<LocalVo> localVoList = LocalService.find(name,searchPreciseKey);
            return localVoList;
        }

        @Override
        protected void onPostExecute(List<LocalVo> localVos) {
            Adapter adapter = new Adapter(localVos);
            recyclerView.setAdapter(adapter);
        }
    }

    class SearchTask extends AsyncTask<Void,Integer,List<LocalVo>>{
        private String name;
        private int searchPreciseKey;

        public SearchTask(String name, int searchPreciseKey) {
            this.name = name;
            this.searchPreciseKey = searchPreciseKey;
        }

        @Override
        protected List<LocalVo> doInBackground(Void... voids) {
            return LocalService.find(name,searchPreciseKey);
        }

        @Override
        protected void onPostExecute(List<LocalVo> localVos) {
            Adapter adapter = new Adapter(localVos);
            recyclerView.setAdapter(adapter);
            fragmentCallback.onSearchSuccess();
        }
    }

    class Adapter extends RecyclerView.Adapter<ViewHolder>{
        private List<LocalVo> localVoList;

        public Adapter(List<LocalVo> localVoList) {
            this.localVoList = localVoList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(activity).inflate(R.layout.item_local_data,viewGroup,false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
            LocalVo localVo = localVoList.get(i);
            LocalPointViewWrap localPointViewWrap = viewHolder.localPointViewWrap;
            PointView pointView = localPointViewWrap.pointView;
            TextView tvName = localPointViewWrap.tvName;
            tvName.setText(localVo.getLocalWave().getName());
            pointView.setPointList(localVo.getLocalWavePointList());
        }

        @Override
        public int getItemCount() {
            return localVoList.size();
        }
    }
    class ViewHolder extends RecyclerView.ViewHolder{
        LocalPointViewWrap localPointViewWrap;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            localPointViewWrap = itemView.findViewById(R.id.local_point_view_wrap);
        }
    }


}
