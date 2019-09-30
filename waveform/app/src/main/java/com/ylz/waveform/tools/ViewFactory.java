package com.ylz.waveform.tools;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import com.ylz.waveform.R;

/**
 * ImageView创建工厂
 */
public class ViewFactory {

	/**
	 * 获取ImageView视图的同时加载显示url
	 * 
	 * @return
	 */
	public static ImageView getImageView(Context context, String url) {
		ImageView imageView = (ImageView)LayoutInflater.from(context).inflate(
				R.layout.view_banner, null);
//		ImageLoader.getInstance().displayImage(url, imageView);
		Picasso.with(context).load(url).into(imageView);
		return imageView;
	}
}
