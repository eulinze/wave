package com.ylz.waveform.presswavecore.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ylz.waveform.R;
import com.ylz.waveform.presswavecore.model.ServerVo;
import com.ylz.waveform.presswavecore.model.db.ServerWave;
import com.ylz.waveform.presswavecore.task.ServerDataTask;
import com.ylz.waveform.presswavecore.viewHolder.ServerViewHolder;
import com.ylz.waveform.presswavecore.widget.LineView;
import com.ylz.waveform.presswavecore.widget.ServerLineViewWrap;

import java.text.SimpleDateFormat;


public class ServerDataAdapter extends BasePageAdapter
                implements BasePageAdapter.CustomInterface {
    private ServerDataTask task;


    public ServerDataAdapter(Context context, int pageSize, ServerDataTask task) {
        super(context,pageSize);
        setCustomInterface(this);
        this.task = task;
        task.setOnLoadFinishCallback(this);
    }

    @Override
    public RecyclerView.ViewHolder getCustomViewHolder(ViewGroup viewGroup) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_server_data,viewGroup,false);
        RecyclerView.ViewHolder viewHolder = new ServerViewHolder(view);
        return viewHolder;
    }

    @Override
    public void load(int pageNo) {
        task.execute(pageNo);
    }

    @Override
    public void bindMyViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        ServerVo serverVo = (ServerVo) dataList.get(position);
        ServerViewHolder serverViewHolder = (ServerViewHolder) viewHolder;
        ServerLineViewWrap serverLineViewWrap = serverViewHolder.serverLineViewWrap;
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

}
