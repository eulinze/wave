package com.ylz.waveform.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.ylz.waveform.R;
import com.ylz.waveform.bean.DeviceBean;
import com.ylz.waveform.tools.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class DeviceAdapter extends BaseAdapter{

	private Context context;
	private int count = 0;
	private List<DeviceBean> list = new ArrayList<>();

	public DeviceAdapter(Context context) {
		this.context = context;
	}

	@Override
	public int getCount() {
		return count;
	}

	@Override
	public DeviceBean getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}


	public void addAll(List<DeviceBean> data) {
		if (data == null || data.isEmpty()) {
			list.clear();
			count = list.size();
			this.notifyDataSetChanged();
		} else {
			list.clear();
			list.addAll(data);
			count = list.size();
			this.notifyDataSetChanged();
		}

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.item_device, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.name = (TextView) convertView.findViewById(R.id.device_name);
			viewHolder.address = (TextView) convertView.findViewById(R.id.device_address);
			if (StringUtil.isNotNull(list.get(position).getName())){
				viewHolder.name.setText(list.get(position).getName());
			}else {
				viewHolder.name.setText("未知名称");
			}

			viewHolder.address.setText(list.get(position).getAddress());

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		return convertView;
	}

	class ViewHolder{
		TextView name;
		TextView address;
	}

}
