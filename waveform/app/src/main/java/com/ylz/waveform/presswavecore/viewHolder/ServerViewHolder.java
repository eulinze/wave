package com.ylz.waveform.presswavecore.viewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.ylz.waveform.R;
import com.ylz.waveform.presswavecore.widget.ServerLineViewWrap;

public class ServerViewHolder extends RecyclerView.ViewHolder {
    public ServerLineViewWrap serverLineViewWrap;
    public ServerViewHolder(@NonNull View itemView) {
        super(itemView);
        serverLineViewWrap = itemView.findViewById(R.id.server_line_view_wrap);
    }
}
