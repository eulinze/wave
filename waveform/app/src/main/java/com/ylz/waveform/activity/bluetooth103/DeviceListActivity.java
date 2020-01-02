package com.ylz.waveform.activity.bluetooth103;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.ylz.waveform.R;
import com.ylz.waveform.activity.bluetooth5.*;
import com.ylz.waveform.adapter.DeviceAdapter;
import com.ylz.waveform.bean.DeviceBean;
import com.ylz.waveform.tools.StringUtil;

import java.util.ArrayList;
import java.util.List;

import static com.ylz.waveform.application.App.EXTRAS_DEVICE_ADDRESS;
import static com.ylz.waveform.application.App.EXTRAS_DEVICE_NAME;

public class DeviceListActivity extends Activity {
    // 调试用
    private static final String TAG = "DeviceListActivity";
    private static final boolean D = true;

    // 成员域
    private BluetoothAdapter mBtAdapter;
    private DeviceAdapter mDeviceAdapter;

    private List<DeviceBean> devicesList ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 创建并显示窗口
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);  //设置窗口显示模式为窗口方式
        setContentView(R.layout.device_list);

        // 设定默认返回值为取消
        setResult(Activity.RESULT_CANCELED);

        // 设定扫描按键响应
        Button scanButton = (Button) findViewById(R.id.button_scan);
        scanButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                doDiscovery();
                v.setVisibility(View.GONE);
            }
        });
        devicesList = new ArrayList<>();
        mDeviceAdapter = new DeviceAdapter(DeviceListActivity.this);

        // 设置新查找设备列表
        ListView devicesListView = (ListView) findViewById(R.id.devices_listview);
        devicesListView.setAdapter(mDeviceAdapter);


        devicesListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 准备连接设备，关闭服务查找
                mBtAdapter.cancelDiscovery();

                // 得到mac地址
                String name = mDeviceAdapter.getItem(position).getName();
                String address = mDeviceAdapter.getItem(position).getAddress();
                // 设置返回数据
                Intent intent = new Intent();
                intent.setClass(DeviceListActivity.this, BleSppActivityForTest.class);
                // 设置返回数据
                intent.putExtra(EXTRAS_DEVICE_NAME, name);
                intent.putExtra(EXTRAS_DEVICE_ADDRESS, address);
                startActivity(intent);
                finish();
            }
        });
        devicesListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // 准备连接设备，关闭服务查找
                mBtAdapter.cancelDiscovery();

                // 得到mac地址
                String name = mDeviceAdapter.getItem(position).getName();
                String address = mDeviceAdapter.getItem(position).getAddress();
                // 设置返回数据
                Intent intent = new Intent();
                intent.setClass(DeviceListActivity.this, ShowReceiveActivity.class);
                // 设置返回数据
                intent.putExtra(EXTRAS_DEVICE_NAME, name);
                intent.putExtra(EXTRAS_DEVICE_ADDRESS, address);
                startActivity(intent);
                finish();
                return false;
            }
        });


        // 注册接收查找到设备action接收器
        this.registerReceiver(mReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));

        // 注册查找结束action接收器
        this.registerReceiver(mReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
        this.registerReceiver(mReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED));
        // 得到本地蓝牙句柄
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 关闭服务查找
        if (mBtAdapter != null) {
            mBtAdapter.cancelDiscovery();
        }

        // 注销action接收器
        this.unregisterReceiver(mReceiver);
    }

    public void OnCancel(View v){
        finish();
    }
    /**
     * 开始服务和设备查找
     */
    private void doDiscovery() {
        if (D) Log.d(TAG, "doDiscovery()");

        // 在窗口显示查找中信息
        setProgressBarIndeterminateVisibility(true);
        setTitle("查找设备中...");

        // 关闭再进行的服务查找
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }

        mBtAdapter.startDiscovery();
        Log.e(TAG,"startDiscovery()");
    }

    // 查找到设备和搜索完成action监听器
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.e(TAG,action);
            // 查找到设备action
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // 得到蓝牙设备
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // 如果是已配对的则略过，已得到显示，其余的在添加到列表中进行显示
                if (devicesList.size()>0){
                    boolean isAlreadyIn = false;
                    for (DeviceBean DeviceBean :devicesList){
                        if (StringUtil.eq(DeviceBean.getAddress(),device.getAddress())){
                            isAlreadyIn = true;
                            break;
                        }
                    }
                    //判断是否已在列表中
                    if (isAlreadyIn){
                    }else{
                        DeviceBean DeviceBean = new DeviceBean();
                        DeviceBean.setAddress(device.getAddress());
                        DeviceBean.setName(device.getName());
                        devicesList.add(DeviceBean);
                        mDeviceAdapter.addAll(devicesList);
                    }
                }else{
                    DeviceBean DeviceBean = new DeviceBean();
                    DeviceBean.setAddress(device.getAddress());
                    DeviceBean.setName(device.getName());
                    devicesList.add(DeviceBean);
                    mDeviceAdapter.addAll(devicesList);
                }
                Log.e(TAG,device.getName() + "---" + device.getAddress());
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                setProgressBarIndeterminateVisibility(false);
            }
        }
    };


}
