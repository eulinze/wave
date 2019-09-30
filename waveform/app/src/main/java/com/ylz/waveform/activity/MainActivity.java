package com.ylz.waveform.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.SeekBar;

import com.ylz.waveform.R;
import com.ylz.waveform.widget.VerticalSeekBar;

public class MainActivity extends AppCompatActivity {

    private VerticalSeekBar verticalSeekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        verticalSeekBar = findViewById(R.id.vertical_Seekbar);
        verticalSeekBar.setMax(8);
        verticalSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            /*
             * SeekBar滚动过程中的回调函数
             */
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }
            /*
             * SeekBar开始滚动的回调函数
             */
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            /*
             * SeekBar停止滚动的回调函数
             */
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

}
