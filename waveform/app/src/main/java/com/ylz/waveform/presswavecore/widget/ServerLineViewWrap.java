package com.ylz.waveform.presswavecore.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ylz.waveform.R;
import com.ylz.waveform.presswavecore.model.db.ServerWavePoint;

import java.util.List;

public class ServerLineViewWrap extends LinearLayout {
    public LineView lineView;
    public TextView tvName;
    public Button compareLineBtn;
    public TextView versionName;
    public TextView downloadTime;
    public ServerLineViewWrap(Context context) {
        super(context);
    }

    public ServerLineViewWrap(Context context, AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(R.layout.server_line_view_wrap,this);
        lineView = view.findViewById(R.id.line_view);
        tvName = view.findViewById(R.id.name);
        compareLineBtn = view.findViewById(R.id.compare_line_btn);
        compareLineBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null != onCompareLineListener){
                    List<ServerWavePoint> pointList = lineView.getPointList();
                    onCompareLineListener.compare(pointList);
                }
            }
        });
        versionName = view.findViewById(R.id.version_name);
        downloadTime = view.findViewById(R.id.download_time);

    }

    public ServerLineViewWrap(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public interface OnCompareLineListener{
        void compare(List<ServerWavePoint> pointList);
    }
    private static OnCompareLineListener onCompareLineListener;

    public static void setOnCompareLineListener(OnCompareLineListener lineListener) {
        onCompareLineListener = lineListener;
    }
}
