//package com.ylz.waveform.activity.bluetooth2;
//
//import android.Manifest;
//import android.app.Activity;
//import android.bluetooth.BluetoothAdapter;
//import android.bluetooth.BluetoothDevice;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.content.pm.PackageManager;
//import android.os.Build;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.ArrayAdapter;
//import android.widget.ListView;
//
//import com.ylz.waveform.R;
//
//public class MainActivity extends Activity implements View.OnClickListener {
//    private final int REQUEST_ENABLE_BT = 0xa01;
//    private final int PERMISSION_REQUEST_COARSE_LOCATION = 0xb01;
//
//    private String TAG = "zhangphil";
//
//    private ArrayAdapter<String> mAdapter;
//    private BluetoothAdapter mBluetoothAdapter;
//
//    // 广播接收发现蓝牙设备
//    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//
//            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
//                Log.d(TAG, "开始扫描...");
//            }
//
//            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
//                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                if (device != null) {
//                    // 添加到ListView的Adapter。
//                    mAdapter.add("设备名:" + device.getName() + "\n设备地址:" + device.getAddress());
//                    mAdapter.notifyDataSetChanged();
//                }
//            }
//
//            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
//                Log.d(TAG, "扫描结束.");
//            }
//        }
//    };
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
//            }
//        }
//
//
//        // 注册广播接收器。
//        // 接收蓝牙发现
//        IntentFilter filterFound = new IntentFilter(BluetoothDevice.ACTION_FOUND);
//        registerReceiver(mReceiver, filterFound);
//
//        IntentFilter filterStart = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
//        registerReceiver(mReceiver, filterStart);
//
//        IntentFilter filterFinish = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
//        registerReceiver(mReceiver, filterFinish);
//
//        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1);
//        ListView listView = ((ListView) findViewById(R.id.listView));
//        listView.setAdapter(mAdapter);
//
//        findViewById(R.id.init).setOnClickListener(this);
//        findViewById(R.id.discovery).setOnClickListener(this);
//        findViewById(R.id.enable_discovery).setOnClickListener(this);
//    }
//
//    @Override
//    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.init:
//                init();
//
//            case R.id.discovery:
//                discovery();
//
//            case R.id.enable_discovery:
//                enable_discovery();
//        }
//    }
//
//    // 初始化蓝牙设备
//    private void init() {
//        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//
//        // 检查设备是否支持蓝牙设备
//        if (mBluetoothAdapter == null) {
//            Log.d(TAG, "设备不支持蓝牙");
//
//            // 不支持蓝牙，退出。
//            return;
//        }
//
//        // 如果用户的设备没有开启蓝牙，则弹出开启蓝牙设备的对话框，让用户开启蓝牙
//        if (!mBluetoothAdapter.isEnabled()) {
//            Log.d(TAG, "请求用户打开蓝牙");
//
//            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
//            // 接下去，在onActivityResult回调判断
//        }
//    }
//
//    // 启动蓝牙发现...
//    private void discovery() {
//        if (mBluetoothAdapter == null) {
//            init();
//        }
//
//        mBluetoothAdapter.startDiscovery();
//    }
//
//    // 可选方法，非必需
//    // 此方法使自身的蓝牙设备可以被其他蓝牙设备扫描到，
//    // 注意时间阈值。0 - 3600 秒。
//    // 通常设置时间为120秒。
//    private void enable_discovery() {
//        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
//
//        // 第二个参数可设置的范围是0~3600秒，在此时间区间（窗口期）内可被发现
//        // 任何不在此区间的值都将被自动设置成120秒。
//        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 3600);
//
//        startActivity(discoverableIntent);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == REQUEST_ENABLE_BT) {
//            if (resultCode == RESULT_OK) {
//                Log.d(TAG, "打开蓝牙成功！");
//            }
//
//            if (resultCode == RESULT_CANCELED) {
//                Log.d(TAG, "放弃打开蓝牙！");
//            }
//
//        } else {
//            Log.d(TAG, "蓝牙异常！");
//        }
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        unregisterReceiver(mReceiver);
//    }
//
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
//        switch (requestCode) {
//            case PERMISSION_REQUEST_COARSE_LOCATION:
//                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//                }
//
//                break;
//        }
//    }
//}
