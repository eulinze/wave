package com.ylz.waveform.activity.bluetooth103;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ylz.waveform.R;
import com.ylz.waveform.presswavecore.MyDataViewPagerAdapter;
import com.ylz.waveform.presswavecore.enums.TabDataSourceEnum;
import com.ylz.waveform.presswavecore.listener.FragmentCallback;
import com.ylz.waveform.presswavecore.listener.SearchListener;
import com.ylz.waveform.presswavecore.model.db.LocalWavePoint;
import com.ylz.waveform.presswavecore.widget.BluePointViewWrap;
import com.ylz.waveform.presswavecore.widget.PointSurfaceView;
import com.ylz.waveform.tools.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.ylz.waveform.application.App.EXTRAS_DEVICE_ADDRESS;
import static com.ylz.waveform.application.App.EXTRAS_DEVICE_NAME;

public class ShowReceiveViewActivity extends AppCompatActivity implements View.OnClickListener , FragmentCallback {

    private String mDeviceName;
    private String mDeviceAddress;
    private BluetoothLeService mBluetoothLeService;
    private boolean mConnected = false;

    public static final String TAG = "ShowReceiveViewActivity";
    private BluePointViewWrap bluePointViewWrap;
    private PointSurfaceView pointSurfaceView;
    private ViewPager viewPager;
    private PagerTabStrip pagerTabStrip;
    private String name = "";

    private final static int REQUEST_CODE_PHONE_STATE = 1;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ListView suggestListView;
    private TabDataSourceEnum witchTab = TabDataSourceEnum.LOCAL_DATA;


    private List<LocalWavePoint> pointList= new ArrayList<>();

    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            mBluetoothLeService.connect(mDeviceAddress);
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {

            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                updateConnectionState(R.string.disconnected);
                invalidateOptionsMenu();
                mBluetoothLeService.connect(mDeviceAddress);
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                //特征值找到才代表连接成功
                mConnected = true;
                invalidateOptionsMenu();
                updateConnectionState(R.string.connected);
            }else if (BluetoothLeService.ACTION_GATT_SERVICES_NO_DISCOVERED.equals(action)){
                mBluetoothLeService.connect(mDeviceAddress);
            }

            else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                String obj = intent.getExtras().getString(BluetoothLeService.EXTRA_DATA);
                byte[] array1 = obj.getBytes();
                displayData(array1);
            }

            else if (BluetoothLeService.ACTION_WRITE_SUCCESSFUL.equals(action)) {
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.gatt_services_characteristics);
        setContentView(R.layout.activity_reveive_view);
        //获取蓝牙的名字和地址
        Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.e(TAG, "Connect request result=" + result);
        }
        init();
    }

    public void init(){
        suggestListView = findViewById(R.id.suggest_list_view);
        bluePointViewWrap = findViewById(R.id.blue_point_view_wrap);
        pointSurfaceView = bluePointViewWrap.getPointView();
        viewPager = findViewById(R.id.view_pager_data);
        pagerTabStrip = findViewById(R.id.pager_tab_strip);
        pagerTabStrip.setTabIndicatorColorResource(R.color.colorAccent);
        pagerTabStrip.setDrawFullUnderline(true);
        MyDataViewPagerAdapter myDataViewPagerAdapter
                = new MyDataViewPagerAdapter(getSupportFragmentManager(),name);
        viewPager.setAdapter(myDataViewPagerAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }
            @Override
            public void onPageSelected(int i) {
                Log.i(TAG, "onPageSelected: "+i);
                witchTab = TabDataSourceEnum.getEnumByKey(i+1);
            }
            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
        navigationView = findViewById(R.id.navigation_view);
        drawerLayout = findViewById(R.id.drawer_layout);
        View headView = navigationView.getHeaderView(0);
        TextView userNameTv = headView.findViewById(R.id.user_name);
        TextView phoneNumberTv = headView.findViewById(R.id.phone_number);
        userNameTv.setText("test");
        phoneNumberTv.setText("test");
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }

    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ShowReceiveViewActivity.this,"链接成功",Toast.LENGTH_LONG).show();
            }
        });
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothLeService.ACTION_WRITE_SUCCESSFUL);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_NO_DISCOVERED);
        return intentFilter;
    }

    //动态效果
    public void convertText(final TextView textView, final int convertTextId) {
        final Animation scaleIn = AnimationUtils.loadAnimation(this,
                R.anim.text_scale_in);
        Animation scaleOut = AnimationUtils.loadAnimation(this,
                R.anim.text_scale_out);
        scaleOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                textView.setText(convertTextId);
                textView.startAnimation(scaleIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        textView.startAnimation(scaleOut);
    }


    private void displayData(byte[] buf) {
        if (buf.length==16){
            byte[] messageTypeByte = new byte[1];
            messageTypeByte[0] = buf[1];

            if ((StringUtil.bytesToString(messageTypeByte).replace(" ", "").equals("01") )) {

                byte[] indexBuf = new byte[2];
                indexBuf[0]=buf[2];
                indexBuf[1]=buf[3];
                String indexString16 = StringUtil.bytesToString(indexBuf).replace(" ","");
                String indexString = Integer.parseInt(indexString16,16)+"";

                byte[] yIntegerBuf = new byte[2];
                yIntegerBuf[0]=buf[4];
                yIntegerBuf[1]=buf[5];
                String yIntegerString16 = StringUtil.bytesToString(yIntegerBuf).replace(" ","");
                String yIntegerString= Integer.parseInt(yIntegerString16,16)+"";

                byte[] yDecimalBuf = new byte[2];
                yDecimalBuf[0]=buf[6];
                yDecimalBuf[1]=buf[7];
                String yDecimalString16 = StringUtil.bytesToString(yDecimalBuf).replace(" ","");
                String yDecimalString= Integer.parseInt(yDecimalString16,16)+"";

                String ySymbolString = "";
                byte[] ySymbolByte = new byte[1];
                ySymbolByte[0] = buf[8];
                if (StringUtil.eq("00",StringUtil.bytesToString(ySymbolByte).replace(" ",""))){
                    ySymbolString = "";
                }else{
                    ySymbolString = "-";
                }

                byte[] xCoordinateBuf = new byte[2];
                xCoordinateBuf[0]=buf[9];
                xCoordinateBuf[1]=buf[10];
                String xCoordinateString16 = StringUtil.bytesToString(xCoordinateBuf).replace(" ","");
                String xCoordinateString= Integer.parseInt(xCoordinateString16,16)+"";

                String xSymbolString = "";
                byte[] xSymbolByte = new byte[1];
                xSymbolByte[0] = buf[11];
                if (StringUtil.eq("00",StringUtil.bytesToString(xSymbolByte).replace(" ",""))){
                    xSymbolString = "";
                }else{
                    xSymbolString = "-";
                }

                byte[] isEndByte = new byte[1];
                isEndByte[0] = buf[14];
                if (StringUtil.eq("01",StringUtil.bytesToString(isEndByte).replace(" ",""))){
                    Toast.makeText(ShowReceiveViewActivity.this,"数据传输完毕",Toast.LENGTH_LONG).show();
                }

                Log.e("------------","第"+indexString+"位采集点，X轴坐标 ："+xSymbolString+xCoordinateString+
                        ",Y轴坐标 ："+ySymbolString+yDecimalString+"\n\n");

//                for (int i = 0 ;i<100;i++){
//                    LocalWavePoint localWavePoint = new LocalWavePoint();
//                    localWavePoint.setId(pointList.size()+1);
//                    localWavePoint.setPressUnit(1);
//                    localWavePoint.setWaveId(pointList.size()+1);
//                    localWavePoint.setX(Float.parseFloat(xCoordinateString));
//                    localWavePoint.setY(Float.parseFloat("3"));
//                    pointList.add(localWavePoint);
//                }
                for(int i=0;i<100;i++){
                    List<LocalWavePoint> localWavePointList = pointSurfaceView.generatePointList();
                    pointSurfaceView.upload(localWavePointList);
                }
                    pointSurfaceView.upload(pointList);
            }
        }else{
            return;
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
        }
    }

    @Override
    public void onSearchSuccess() {
        suggestListView.setVisibility(View.GONE);
    }

    @Override
    public void onSearchFail() {
    }

    @Override
    public void addObserver(SearchListener observer){
//        observerList.add(observer);
    }
}
