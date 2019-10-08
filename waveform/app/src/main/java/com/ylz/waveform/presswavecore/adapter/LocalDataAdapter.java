package com.ylz.waveform.presswavecore.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ylz.waveform.R;
import com.ylz.waveform.presswavecore.model.LocalVo;
import com.ylz.waveform.presswavecore.task.LocalDataTask;
import com.ylz.waveform.presswavecore.viewHolder.LocalViewHolder;
import com.ylz.waveform.presswavecore.widget.LocalPointViewWrap;
import com.ylz.waveform.presswavecore.widget.PointView;


public class LocalDataAdapter extends BasePageAdapter
                implements BasePageAdapter.CustomInterface {
    private LocalDataTask task;


    public LocalDataAdapter(Context context, int pageSize,LocalDataTask task) {
        super(context,pageSize);
        setCustomInterface(this);
        this.task = task;
        task.setOnLoadFinishCallback(this);
    }

    @Override
    public RecyclerView.ViewHolder getCustomViewHolder(ViewGroup viewGroup) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_local_data,viewGroup,false);
        RecyclerView.ViewHolder viewHolder = new LocalViewHolder(view);
        return viewHolder;
    }

    @Override
    public void load(int pageNo) {
        task.execute(pageNo);
    }

    @Override
    public void bindMyViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        LocalViewHolder localViewHolder = (LocalViewHolder) viewHolder;
        LocalVo localVo = (LocalVo) dataList.get(position);
        LocalPointViewWrap localPointViewWrap = localViewHolder.localPointViewWrap;
        PointView pointView = localPointViewWrap.pointView;
        TextView tvName = localPointViewWrap.tvName;
        tvName.setText(localVo.getLocalWave().getName());
        pointView.setPointList(localVo.getLocalWavePointList());
    }

}
