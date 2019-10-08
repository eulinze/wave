package com.ylz.waveform.presswavecore.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ylz.waveform.R;

public class LocalPointViewWrap extends LinearLayout {
    public PointView pointView;
    public TextView tvName;
    public LocalPointViewWrap(Context context) {
        super(context);
    }

    public LocalPointViewWrap(Context context,  AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(R.layout.local_point_view_wrap,this);
        pointView = view.findViewById(R.id.point_view);
        tvName = view.findViewById(R.id.name);
    }

    public LocalPointViewWrap(Context context,  AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
