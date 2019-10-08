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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.ylz.waveform.activity.presswave.PressWaveApplication;
import com.ylz.waveform.R;
import com.ylz.waveform.activity.presswave.dao.ServerWaveDao;
import com.ylz.waveform.activity.presswave.dao.ServerWavePointDao;
import com.ylz.waveform.activity.presswave.enums.SearchPreciseEnum;
import com.ylz.waveform.activity.presswave.listener.FragmentCallback;
import com.ylz.waveform.activity.presswave.model.ServerVo;
import com.ylz.waveform.activity.presswave.model.db.ServerWave;
import com.ylz.waveform.activity.presswave.model.db.ServerWavePoint;
import com.ylz.waveform.activity.presswave.model.gson.GsonUtils;
import com.ylz.waveform.activity.presswave.model.gson.ServerDataGson;
import com.ylz.waveform.activity.presswave.service.ServerService;
import com.ylz.waveform.activity.presswave.utils.OkHttpUtil;
import com.ylz.waveform.activity.presswave.utils.ToastUtils;
import com.ylz.waveform.activity.presswave.widget.LineView;
import com.ylz.waveform.activity.presswave.widget.RecyclerViewDivider;
import com.ylz.waveform.activity.presswave.widget.ServerLineViewWrap;
import com.ylz.waveform.presswavecore.Config;
import com.ylz.waveform.presswavecore.enums.TabDataSourceEnum;
import com.ylz.waveform.presswavecore.listener.SearchListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ServerDataFragment extends Fragment implements SearchListener {
    private static final String TAG = "ServerDataFragment";
    private Activity activity;
    private RecyclerView recyclerView;
    private String url = Config.URL_DOWNLOAD_SERVER_DATA;
    private String name = "";
    private int searchPreciseKey = 0;
    private static FragmentCallback fragmentCallback;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = getActivity();
        fragmentCallback = (FragmentCallback) activity;
        fragmentCallback.addObserver(this);
    }

    public static ServerDataFragment newInstance(String name, int searchPreciseKey){
        ServerDataFragment fragment = new ServerDataFragment();
        Bundle bundle = new Bundle();
        bundle.putString("name",name);
        bundle.putInt("searchPreciseKey",searchPreciseKey);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if(null != getArguments()){
            Bundle bundle = getArguments();
            name = bundle.getString("name");
            searchPreciseKey = bundle.getInt("searchPreciseKey");
        }
        View view = inflater.inflate(R.layout.fragment_server_data,container,false);
        recyclerView = view.findViewById(R.id.server_data_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new RecyclerViewDivider(
                activity,
                LinearLayoutManager.HORIZONTAL,
                4, getResources().getColor(R.color.divide_color)));
        new ServerDataLoadTask().execute(name,searchPreciseKey);

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
                                ToastUtils.toast("获取服务器数据失败");
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
            new SearchTask(text, SearchPreciseEnum.PRECISE.getKey()).execute();
        }
    }

    @Override
    public void onSearch(TabDataSourceEnum witchTab, String text) {
        if(TabDataSourceEnum.SERVICE_DATA.equals(witchTab)){
            new SearchTask(text,SearchPreciseEnum.FUZZY.getKey()).execute();
        }
    }

    class SearchTask extends AsyncTask<Void,Integer,List<ServerVo>>{
        private String name;
        private int searchPreciseKey;

        public SearchTask(String name, int searchPreciseKey) {
            this.name = name;
            this.searchPreciseKey = searchPreciseKey;
        }

        @Override
        protected List<ServerVo> doInBackground(Void... voids) {
            return ServerService.find(name,searchPreciseKey);
        }

        @Override
        protected void onPostExecute(List<ServerVo> serverVos) {
            Adapter adapter = new Adapter(serverVos);
            recyclerView.setAdapter(adapter);
            fragmentCallback.onSearchSuccess();
        }
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
                new ServerDataLoadTask().execute(name,searchPreciseKey);
            }
            ToastUtils.toast(s);
        }
    }
    class ServerDataLoadTask extends AsyncTask<Object,Integer, List<ServerVo>> {

        @Override
        protected List<ServerVo> doInBackground(Object... objects) {

            String name = (String) objects[0];
            int searchPreciseKey = (int) objects[1];
            return ServerService.find(name,searchPreciseKey);
        }

        @Override
        protected void onPostExecute(List<ServerVo> serverVoList) {
            Adapter adapter = new Adapter(serverVoList);
            recyclerView.setAdapter(adapter);
        }
    }
    class Adapter extends RecyclerView.Adapter<ViewHolder>{
        private List<ServerVo> serverVoList;

        public Adapter(List<ServerVo> serverVoList) {
            this.serverVoList = serverVoList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(activity).inflate(R.layout.item_server_data,viewGroup,false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
            ServerVo serverVo = serverVoList.get(i);
            ServerLineViewWrap serverLineViewWrap = viewHolder.serverLineViewWrap;
            LineView lineView = serverLineViewWrap.lineView;
            TextView tvName = serverLineViewWrap.tvName;
            TextView versionName = serverLineViewWrap.versionName;
            TextView downloadTime = serverLineViewWrap.downloadTime;
            ServerWave wave = serverVo.getServerWave();
            tvName.setText(wave.getName());
            versionName.setText(wave.getVersionName());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String downloadTimeStr = "";
            if(null != wave.getDownloadTime()){
                downloadTimeStr = sdf.format(wave.getDownloadTime());
            }
            downloadTime.setText(downloadTimeStr);
            lineView.setPointList(serverVo.getServerWavePointList());
        }

        @Override
        public int getItemCount() {
            return serverVoList.size();
        }
    }
    class ViewHolder extends RecyclerView.ViewHolder{
        ServerLineViewWrap serverLineViewWrap;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            serverLineViewWrap = itemView.findViewById(R.id.server_line_view_wrap);
        }
    }


}
