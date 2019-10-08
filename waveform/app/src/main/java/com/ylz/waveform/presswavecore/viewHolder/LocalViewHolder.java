package com.ylz.waveform.presswavecore.viewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.ylz.waveform.R;
import com.ylz.waveform.presswavecore.widget.LocalPointViewWrap;

public class LocalViewHolder extends RecyclerView.ViewHolder {
    public LocalPointViewWrap localPointViewWrap;
    public LocalViewHolder(@NonNull View itemView) {
        super(itemView);
        localPointViewWrap = itemView.findViewById(R.id.local_point_view_wrap);
    }
}
