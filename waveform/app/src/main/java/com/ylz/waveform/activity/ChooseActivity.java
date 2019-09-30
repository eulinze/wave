package com.ylz.waveform.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import com.ylz.waveform.R;
import com.ylz.waveform.widget.VerticalSeekBar;

public class ChooseActivity extends AppCompatActivity {

    private Button fourButton;
    private Button twoFragmentButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_activity_layout);
        fourButton = super.findViewById(R.id.four_activity);
        twoFragmentButton = super.findViewById(R.id.two_fragment_activity);

        fourButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent();
                it.setClass(ChooseActivity.this,NiaojianActivity.class);
                startActivity(it);
            }
        });

        twoFragmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent();
                it.setClass(ChooseActivity.this,TwoFragmentActivity.class);
                startActivity(it);
            }
        });
    }

}
