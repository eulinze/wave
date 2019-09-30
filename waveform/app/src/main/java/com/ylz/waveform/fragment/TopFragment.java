package com.ylz.waveform.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ylz.waveform.R;
import com.ylz.waveform.base.BaseFragment;
import com.ylz.waveform.widget.TwoLineGraphicView;
import com.ylz.waveform.widget.VerticalSeekBar;

import java.util.ArrayList;

/**
 * Created by eulinze on 2019/7/20.
 */

public class TopFragment extends BaseFragment implements View.OnClickListener{
    private ViewGroup view;

    ArrayList<Double> yListOriginal = new ArrayList<Double>();
    ArrayList<Double> y1ListOriginal = new ArrayList<Double>();

    ArrayList<Double> y1List= new ArrayList<>();
    ArrayList<String> xRawDatas;
    TwoLineGraphicView tu;

    private ImageView middleZuoImageView;//middle_zou_imageview
    private ImageView middleYuoImageView;//middle_you_imageview

    private int middleDeviation = 0;

    private VerticalSeekBar verticalSeekbar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = (ViewGroup) inflater.inflate(R.layout.top_fragment_layout, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        setListener();
    }

    public void initView() {

        middleZuoImageView = (ImageView) view.findViewById(R.id.middle_zou_imageview);
        middleYuoImageView = (ImageView) view.findViewById(R.id.middle_you_imageview);

        verticalSeekbar =  view.findViewById(R.id.vertical_seekbar);
        verticalSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Toast.makeText(getActivity(), "当前扩展" + progress + "倍", Toast.LENGTH_SHORT).show();
                setxRawDatas(progress);
//                tu.setData(yListOriginal,y1List, xRawDatas, 10, 1);
//                tu.invalidate();
                refreshView();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void setListener() {
        middleZuoImageView.setOnClickListener(this);
        middleYuoImageView.setOnClickListener(this);

        tu = (TwoLineGraphicView) view.findViewById(R.id.line_graphic);
        initOrginalData();

    }

    public void initOrginalData(){
        yListOriginal.add((double) 2.103);
        yListOriginal.add(4.05);
        yListOriginal.add(6.60);
        yListOriginal.add(3.08);
        yListOriginal.add(4.32);
        yListOriginal.add(2.0);
        yListOriginal.add(1.0);
        yListOriginal.add(2.5);
        yListOriginal.add(4.6);
        yListOriginal.add(3.2);
        yListOriginal.add(1.5);
        yListOriginal.add(3.5);

        y1ListOriginal.add((double) 5.103);
        y1ListOriginal.add(4.05);
        y1ListOriginal.add(3.60);
        y1ListOriginal.add(6.08);
        y1ListOriginal.add(1.32);
        y1ListOriginal.add(3.0);
        y1ListOriginal.add(3.0);

        xRawDatas = new ArrayList<String>();
        setxRawDatas(1);

        tu.setData(yListOriginal,y1ListOriginal, xRawDatas, 10, 1);
        tu.invalidate();
    }

    private void setxRawDatas(int multiple){
        xRawDatas.clear();
        for (int i = 0; i<16*multiple;i++){
            xRawDatas.add(i+"");
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.middle_zou_imageview:
                middleDeviation--;
                refreshView();
                break;
            case R.id.middle_you_imageview:
                middleDeviation++;
                refreshView();
                break;
            default:
                break;
        }
    }

    public void refreshView(){
        y1List.clear();

        if (middleDeviation<0){
            for (int i = 0-middleDeviation ; i<y1ListOriginal.size();i++){
                y1List.add(y1ListOriginal.get(i));
            }
        }
        if (middleDeviation>=0){
            for (int i = 0 ;i<middleDeviation;i++){
                y1List.add(0.0);
            }
            for (int i = 0 ;i<y1ListOriginal.size();i++){
                if(y1List.size()>=y1ListOriginal.size()){
                    continue;
                }
                y1List.add(y1ListOriginal.get(i));
            }
        }

        tu.setData(yListOriginal,y1List, xRawDatas, 10, 1);
        tu.invalidate();
    }
}
