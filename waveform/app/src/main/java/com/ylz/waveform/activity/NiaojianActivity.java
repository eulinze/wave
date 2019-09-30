package com.ylz.waveform.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.ylz.waveform.R;
import com.ylz.waveform.widget.LineGraphicView;

import java.util.ArrayList;
import java.util.List;

public class NiaojianActivity extends Activity  implements OnClickListener
{
	ArrayList<Double> yListOriginal = new ArrayList<Double>();
	ArrayList<Double> y1ListOriginal = new ArrayList<Double>();
	ArrayList<Double> y2ListOriginal = new ArrayList<Double>();
	ArrayList<Double> y3ListOriginal = new ArrayList<Double>();

	ArrayList<Double> yList = new ArrayList<>();
	ArrayList<Double> y1List= new ArrayList<>();
	ArrayList<Double> y2List= new ArrayList<>();
	ArrayList<Double> y3List= new ArrayList<>();
	ArrayList<String> xRawDatas;
	LineGraphicView tu;

	private TextView biggerTextview;
	private TextView smallTextview;

	private ImageView topZuoImageView;//top_zuo_imageview
	private ImageView topYuoImageView;//top_you_imageview
	private ImageView middleZuoImageView;//middle_zou_imageview
	private ImageView middleYuoImageView;//middle_you_imageview
	private ImageView bottomZuoImageView;//bottom_zou_imageview
	private ImageView bottomYuoImageView;//bottom_you_imageview

	private int topDeviation = 0;
	private int middleDeviation = 0;
	private int bottomDeviation = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.niaojian);

		biggerTextview=(TextView)findViewById(R.id.bigger_textview);
		smallTextview=(TextView)findViewById(R.id.small_textview);

		topZuoImageView = (ImageView) findViewById(R.id.top_zuo_imageview);
		topYuoImageView = (ImageView) findViewById(R.id.top_you_imageview);
		middleZuoImageView = (ImageView) findViewById(R.id.middle_zou_imageview);
		middleYuoImageView = (ImageView) findViewById(R.id.middle_you_imageview);
		bottomZuoImageView = (ImageView) findViewById(R.id.bottom_zou_imageview);
		bottomYuoImageView = (ImageView) findViewById(R.id.bottom_you_imageview);

		topZuoImageView.setOnClickListener(this);
		topYuoImageView.setOnClickListener(this);
		middleZuoImageView.setOnClickListener(this);
		middleYuoImageView.setOnClickListener(this);
		bottomZuoImageView.setOnClickListener(this);
		bottomYuoImageView.setOnClickListener(this);

		tu = (LineGraphicView) findViewById(R.id.line_graphic);
		initOrginalData();

		biggerTextview.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				tu.setData(yListOriginal,y1List,y2List,y3List, xRawDatas, 8, 1);
				tu.invalidate();
			}
		});
		smallTextview.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				tu.setData(yListOriginal,y1List,y2List,y3List, xRawDatas, 16, 2);
				tu.invalidate();
			}
		});
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

		y2ListOriginal.add((double) 6.103);
		y2ListOriginal.add(2.05);
		y2ListOriginal.add(7.60);
		y2ListOriginal.add(1.08);
		y2ListOriginal.add(3.32);
		y2ListOriginal.add(2.0);
		y2ListOriginal.add(5.0);

		y3ListOriginal.add((double) 1.103);
		y3ListOriginal.add(4.05);
		y3ListOriginal.add(3.60);
		y3ListOriginal.add(5.08);
		y3ListOriginal.add(7.32);
		y3ListOriginal.add(2.0);
		y3ListOriginal.add(6.0);

		xRawDatas = new ArrayList<String>();
		xRawDatas.add("0");
		xRawDatas.add("1");
		xRawDatas.add("2");
		xRawDatas.add("3");
		xRawDatas.add("4");
		xRawDatas.add("5");
		xRawDatas.add("6");
		xRawDatas.add("7");
		xRawDatas.add("8");
		xRawDatas.add("9");
		xRawDatas.add("10");
		xRawDatas.add("11");
		tu.setData(yListOriginal,y1ListOriginal,y2ListOriginal,y3ListOriginal, xRawDatas, 10, 1);
		tu.invalidate();
	}

	private void clearShowList(){
		y1List.clear();
		y2List.clear();
		y3List.clear();
	}

	public void refreshView(){
		clearShowList();

		if (topDeviation<0){
//			for (int i = 0 ;i<0-topDeviation;i++){
////				y1List.add(0.0);
//			}
			for (int i = 0-topDeviation ; i<y1ListOriginal.size();i++){
				y1List.add(y1ListOriginal.get(i));
			}
		}
		if (topDeviation>=0){
			for (int i = 0 ;i<topDeviation;i++){
				y1List.add(0.0);
			}
			for (int i = 0 ;i<y1ListOriginal.size();i++){
				if(y1List.size()>=yListOriginal.size()){
					continue;
				}
				y1List.add(y1ListOriginal.get(i));
			}
		}

		if (middleDeviation<0){
//			for (int i = 0 ;i<0-middleDeviation;i++){
////				y2List.add(0.0);
//			}
			for (int i = 0-middleDeviation ; i<y2ListOriginal.size();i++){
				y2List.add(y2ListOriginal.get(i));
			}
		}
		if (middleDeviation>=0){
			for (int i = 0 ;i<middleDeviation;i++){
				y2List.add(0.0);
			}
			for (int i = 0 ;i<y2ListOriginal.size();i++){
				if(y2List.size()>=yListOriginal.size()){
					continue;
				}
				y2List.add(y2ListOriginal.get(i));
			}
		}


		if (bottomDeviation<0){
//			for (int i = 0 ;i<0-bottomDeviation;i++){
////				y3List.add(0.0);
//			}
			for (int i = 0-bottomDeviation ; i<y3ListOriginal.size();i++){
				y3List.add(y3ListOriginal.get(i));
			}
		}
		if (bottomDeviation>=0){
			for (int i = 0 ;i<bottomDeviation;i++){
				y3List.add(0.0);
			}
			for (int i = 0 ;i<y3ListOriginal.size();i++){
				if(y3List.size()>=yListOriginal.size()){
					continue;
				}
				y3List.add(y3ListOriginal.get(i));
			}
		}

		tu.setData(yListOriginal,y1List,y2List,y3List, xRawDatas, 10, 1);
		tu.invalidate();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.top_zuo_imageview:
				topDeviation--;
				refreshView();
				break;
			case R.id.top_you_imageview:
				topDeviation++;
				refreshView();
				break;
			case R.id.middle_zou_imageview:
				middleDeviation--;
				refreshView();
				break;
			case R.id.middle_you_imageview:
				middleDeviation++;
				refreshView();
				break;
			case R.id.bottom_zou_imageview:
				bottomDeviation--;
				refreshView();
				break;
			case R.id.bottom_you_imageview:
				bottomDeviation++;
				refreshView();
				break;
			default:
				break;
		}
	}
}
