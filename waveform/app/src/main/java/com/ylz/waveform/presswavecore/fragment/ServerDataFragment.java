package com.ylz.waveform.presswavecore.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.ylz.waveform.presswavecore.Config;
import com.ylz.waveform.R;
import com.ylz.waveform.presswavecore.adapter.ServerDataAdapter;
import com.ylz.waveform.presswavecore.callback.OnRefreshCallback;
import com.ylz.waveform.presswavecore.dao.ServerWaveDao;
import com.ylz.waveform.presswavecore.dao.ServerWavePointDao;
import com.ylz.waveform.presswavecore.enums.SearchPreciseEnum;
import com.ylz.waveform.presswavecore.enums.TabDataSourceEnum;
import com.ylz.waveform.presswavecore.listener.FragmentCallback;
import com.ylz.waveform.presswavecore.listener.SearchListener;
import com.ylz.waveform.presswavecore.model.ServerVo;
import com.ylz.waveform.presswavecore.model.db.ServerWave;
import com.ylz.waveform.presswavecore.model.db.ServerWavePoint;
import com.ylz.waveform.presswavecore.model.gson.GsonUtils;
import com.ylz.waveform.presswavecore.model.gson.ServerDataGson;
import com.ylz.waveform.presswavecore.task.ServerDataTask;
import com.ylz.waveform.presswavecore.utils.OkHttpUtil;
import com.ylz.waveform.presswavecore.utils.ToastUtils;
import com.ylz.waveform.presswavecore.widget.RecyclerViewDivider;
import java.io.IOException;
import java.util.List;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ServerDataFragment extends Fragment implements SearchListener, OnRefreshCallback {
    private static final String TAG = "ServerDataFragment";
    private Activity activity;
    private RecyclerView recyclerView;
//    private SwipeRefreshLayout swipeRefreshLayout;
    private String url = Config.URL_DOWNLOAD_SERVER_DATA;
    private String name = "";
    private static FragmentCallback fragmentCallback;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = getActivity();
        fragmentCallback = (FragmentCallback) activity;
        fragmentCallback.addObserver(this);
    }

    public static ServerDataFragment newInstance(String name){
        ServerDataFragment fragment = new ServerDataFragment();
        Bundle bundle = new Bundle();
        bundle.putString("name",name);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if(null != getArguments()){
            Bundle bundle = getArguments();
            name = bundle.getString("name");
        }
        View view = inflater.inflate(R.layout.fragment_server_data,container,false);
        recyclerView = view.findViewById(R.id.server_data_recycler_view);
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
//                refresh(name,SearchPreciseEnum.FUZZY.getKey());
//            }
//        });
        Button serverDataDownloadBtn = view.findViewById(R.id.server_data_download_btn);
        serverDataDownloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OkHttpUtil.get(url, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtils.toast(activity,"获取服务器数据失败");
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String json = response.body().string();
                        Log.i(TAG, "onResponse: "+json);
                        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                        List<ServerDataGson> serverDataGsons = gson.fromJson(json,new TypeToken<List<ServerDataGson>>(){}.getType());
                        new SaveServerDataToLocalTask(serverDataGsons).execute();
                    }
                });
            }
        });
        return view;
    }

    @Override
    public void onItemClick(TabDataSourceEnum witchTab, String text) {
        if(TabDataSourceEnum.SERVICE_DATA.equals(witchTab)){
            refresh(text,SearchPreciseEnum.PRECISE.getKey());
            fragmentCallback.onSearchSuccess();
        }
    }

    @Override
    public void onSearch(TabDataSourceEnum witchTab, String text) {
        if(TabDataSourceEnum.SERVICE_DATA.equals(witchTab)){
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


    class SaveServerDataToLocalTask extends AsyncTask<Object,Integer,String>{
        private List<ServerDataGson> serverDataGsonList;

        public SaveServerDataToLocalTask(List<ServerDataGson> serverDataGsonList) {
            this.serverDataGsonList = serverDataGsonList;
        }

        @Override
        protected String doInBackground(Object... objects) {
            try{
                ServerWaveDao.deleteAll();
                ServerWavePointDao.deleteAll();
                for(ServerDataGson gson:serverDataGsonList){
                    ServerVo vo = GsonUtils.convert(gson);
                    ServerWave serverWave = vo.getServerWave();
                    List<ServerWavePoint> serverWavePoints = vo.getServerWavePointList();
                    ServerWaveDao.save(serverWave);
                    int waveId = serverWave.getId();
                    for(ServerWavePoint point:serverWavePoints){
                        point.setWaveId(waveId);
                    }
                    ServerWavePointDao.saveBatch(serverWavePoints);

                }
            }catch (Exception e){
                e.printStackTrace();
                return "下载失败";
            }
            return "下载成功";
        }

        @Override
        protected void onPostExecute(String s) {
            if("下载成功".equals(s)){
                refresh(name,SearchPreciseEnum.FUZZY.getKey());
            }
            ToastUtils.toast(activity,s);
        }
    }

    public void refresh(String name,int searchPreciseKey){
        ServerDataTask task = new ServerDataTask(name,searchPreciseKey);
        ServerDataAdapter adapter = new ServerDataAdapter(activity,Config.SERVER_DATA_PAGE_SIZE,task);
        adapter.setOnRefreshCallback(this);
        recyclerView.setAdapter(adapter);
    }

}
