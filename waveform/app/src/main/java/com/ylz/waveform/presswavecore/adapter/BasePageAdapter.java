package com.ylz.waveform.presswavecore.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ylz.waveform.R;
import com.ylz.waveform.presswavecore.callback.OnLoadFinishCallback;
import com.ylz.waveform.presswavecore.callback.OnRefreshCallback;
import com.ylz.waveform.presswavecore.model.CommonData;
import com.ylz.waveform.presswavecore.viewHolder.LocalViewHolder;
import com.ylz.waveform.presswavecore.viewHolder.ServerViewHolder;

import java.util.ArrayList;
import java.util.List;

public class BasePageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements OnLoadFinishCallback {

    public Context context;

    public List<CommonData> dataList = new ArrayList<>();




    public BasePageAdapter(Context context,int pageSize) {
        this.context = context;
        this.pageSize = pageSize;
    }

    public final static int ITEM_TYPE_HAS_MORE = 1;
    public final static int ITEM_TYPE_NO_MORE = 2;
    public volatile int itemType = ITEM_TYPE_HAS_MORE;
    public int pageNo = 1;
    private int pageSize;



    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int type) {
        RecyclerView.ViewHolder viewHolder = null;
        if(ITEM_TYPE_HAS_MORE == type){
            View view = LayoutInflater.from(context).inflate(R.layout.item_has_more,viewGroup,false);
            viewHolder = new HasMoreViewHolder(view);
        }else if(ITEM_TYPE_NO_MORE == type){
            View view = LayoutInflater.from(context).inflate(R.layout.item_no_more,viewGroup,false);
            viewHolder = new NoMoreViewHolder(view);
        }else{
            if(null != customInterface){
                viewHolder = customInterface.getCustomViewHolder(viewGroup);
            }
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if(viewHolder.getClass().equals(HasMoreViewHolder.class)){
            if(null != customInterface)
                customInterface.load(pageNo++);
        }else if(viewHolder.getClass().equals(LocalViewHolder.class)
                || viewHolder.getClass().equals(ServerViewHolder.class)){
            if(null != customInterface)
                customInterface.bindMyViewHolder(viewHolder,i);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(position == dataList.size()){
            return itemType;
        }
        return -1;
    }

    @Override
    public int getItemCount() {
        return dataList.size()+1;
    }

    @Override
    public void hasMore(List<? extends CommonData> dataList) {
        itemType = ITEM_TYPE_HAS_MORE;
        int index = (pageNo-2)* pageSize;
        this.dataList.addAll(index,dataList);
        this.notifyItemRangeChanged(index,dataList.size());
        if(null != onRefreshCallback){
            onRefreshCallback.onRefreshSuccess();
        }
    }

    @Override
    public void noMore(List<? extends CommonData> dataList) {
        itemType = ITEM_TYPE_NO_MORE;
        int index = (pageNo-2)* pageSize;
        this.dataList.addAll(index,dataList);
        //userWaveList.size()+1,加1是为了防止一直转圈，更新最后一个item的状态
        this.notifyItemRangeChanged(index,dataList.size()+1);
        if(null != onRefreshCallback){
            onRefreshCallback.onRefreshSuccess();
        }
    }


    class NoMoreViewHolder extends RecyclerView.ViewHolder{

        public NoMoreViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
    class HasMoreViewHolder extends RecyclerView.ViewHolder{

        public HasMoreViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public interface CustomInterface{
        RecyclerView.ViewHolder getCustomViewHolder(ViewGroup viewGroup);

        void load(int pageNo);

        void bindMyViewHolder(RecyclerView.ViewHolder viewHolder, int position);


    }
    private CustomInterface customInterface;

    public void setCustomInterface(CustomInterface customInterface) {
        this.customInterface = customInterface;
    }

    private OnRefreshCallback onRefreshCallback;

    public void setOnRefreshCallback(OnRefreshCallback onRefreshCallback) {
        this.onRefreshCallback = onRefreshCallback;
    }
}
