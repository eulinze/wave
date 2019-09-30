package com.ylz.waveform.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.ylz.waveform.R;
import com.ylz.waveform.fragment.BottomFragment;
import com.ylz.waveform.fragment.TopFragment;

public class TwoFragmentActivity extends AppCompatActivity
{

	private TopFragment topFragment;
	private BottomFragment bottomFragment;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.two_fragment_layout);
        initView();
	}
	public void initView(){
        initFragment();
	}

	private void initFragment() {
        if (topFragment == null) {
            topFragment = new TopFragment();
        }

        if (!topFragment.isAdded()) {
            // 提交事务
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.top_fragment_content, topFragment).commit();

        }

		if (bottomFragment == null) {
            bottomFragment = new BottomFragment();
		}

		if (!bottomFragment.isAdded()) {
			// 提交事务
			getSupportFragmentManager().beginTransaction()
					.add(R.id.bottom_fragment_content, bottomFragment).commit();

		}

	}


}
