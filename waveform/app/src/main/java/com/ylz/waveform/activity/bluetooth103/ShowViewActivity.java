package com.ylz.waveform.activity.bluetooth103;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ylz.waveform.R;
import com.ylz.waveform.presswavecore.MyDataViewPagerAdapter;
import com.ylz.waveform.presswavecore.dao.LocalWaveDao;
import com.ylz.waveform.presswavecore.dao.LocalWavePointDao;
import com.ylz.waveform.presswavecore.dao.ServerWaveDao;
import com.ylz.waveform.presswavecore.enums.SearchPreciseEnum;
import com.ylz.waveform.presswavecore.enums.TabDataSourceEnum;
import com.ylz.waveform.presswavecore.listener.FragmentCallback;
import com.ylz.waveform.presswavecore.listener.SearchListener;
import com.ylz.waveform.presswavecore.model.db.LocalWave;
import com.ylz.waveform.presswavecore.model.db.LocalWavePoint;
import com.ylz.waveform.presswavecore.model.db.ServerWave;
import com.ylz.waveform.presswavecore.utils.ToastUtils;
import com.ylz.waveform.presswavecore.utils.Utils;
import com.ylz.waveform.presswavecore.widget.BluePointViewWrap;
import com.ylz.waveform.presswavecore.widget.PointSurfaceView;
import com.ylz.waveform.tools.StringUtil;
import java.util.ArrayList;
import java.util.List;
import static com.ylz.waveform.application.App.EXTRAS_DEVICE_ADDRESS;
import static com.ylz.waveform.application.App.EXTRAS_DEVICE_NAME;

public class ShowViewActivity extends AppCompatActivity  implements FragmentCallback {
    public static final String TAG = "ShowViewActivity";
    private BluePointViewWrap bluePointViewWrap;
    private PointSurfaceView pointSurfaceView;
    private ViewPager viewPager;
    private PagerTabStrip pagerTabStrip;
    private String name = "";
    //默认模糊搜索
    private int searchPreciseKey = SearchPreciseEnum.FUZZY.getKey();
    private TabDataSourceEnum witchTab = TabDataSourceEnum.LOCAL_DATA;

    private SearchView mSearchView;
    private SearchView.SearchAutoComplete searchAutoComplete;
    private ListView suggestListView;
    private List<SearchListener> observerList = new ArrayList<>();

    private final static int REQUEST_CODE_PHONE_STATE = 1;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;

    private String mDeviceName;
    private String mDeviceAddress;
    private BluetoothLeService mBluetoothLeService;
    private boolean mConnected = false;

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
                mBluetoothLeService.connect(mDeviceAddress);
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                //特征值找到才代表连接成功
                mConnected = true;
                updateConnectionState(R.string.connected);
            }else if (BluetoothLeService.ACTION_GATT_SERVICES_NO_DISCOVERED.equals(action)){
                mBluetoothLeService.connect(mDeviceAddress);
            }

            else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                String obj = intent.getExtras().getString(BluetoothLeService.EXTRA_DATA);
                byte[] array1 = obj.getBytes();
                Log.e("***********",obj);
                displayData(array1);
            }
            else if (BluetoothLeService.ACTION_WRITE_SUCCESSFUL.equals(action)) {
//                mSendBytes.setText(sendBytes + " ");
//                if (sendDataLen>0)
//                {
//                    Log.v("log","Write OK,Send again");
//                }
//                else {
//                    Log.v("log","Write Finish");
//                }
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

        int granted = ContextCompat.checkSelfPermission(this,"Manifest.permission.READ_PHONE_STATE");
        if(PackageManager.PERMISSION_GRANTED == granted){
            init();
        }else{
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_PHONE_STATE},REQUEST_CODE_PHONE_STATE);
        }

        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.e(TAG, "Connect request result=" + result);
        }
    }
    public void init(){
        // 使用Toolbar代替actionbar
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        //通过MenuItem得到SearchView
        mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        //设置是否显示搜索框展开时的提交按钮
        mSearchView.setSubmitButtonEnabled(true);
        //mSearchView.setIconifiedByDefault(false);
        mSearchView.setFocusable(false);
        //设置输入框提示文字样式

        searchAutoComplete = mSearchView.findViewById(R.id.search_src_text);
        searchAutoComplete.setHintTextColor(getResources().getColor(android.R.color.darker_gray));
        searchAutoComplete.setTextColor(getResources().getColor(android.R.color.background_light));
        searchAutoComplete.setTextSize(14);

        mSearchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    if(TabDataSourceEnum.LOCAL_DATA.equals(witchTab)){
                        new LocalSuggestTask("").execute();
                    }else{
                        new ServerSuggestTask("").execute();
                    }
                }
            }
        });
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Utils.toggleSoftInput(ShowViewActivity.this);
                mSearchView.clearFocus();
                for(SearchListener callback:observerList){
                    callback.onSearch(witchTab,s);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if(TabDataSourceEnum.LOCAL_DATA.equals(witchTab)){
                    new LocalSuggestTask(s).execute();
                }else{
                    new ServerSuggestTask(s).execute();
                }
                return true;
            }
        });
        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                suggestListView.setVisibility(View.GONE);
                return false;
            }
        });
        return true;
    }

    @Override
    public void onSearchSuccess() {
        suggestListView.setVisibility(View.GONE);
    }

    @Override
    public void onSearchFail() {
    }

    class LocalSuggestTask extends AsyncTask<Void,Integer,List<LocalWave>> {
        private String name;

        public LocalSuggestTask(String name) {
            this.name = name;
        }

        @Override
        protected List<LocalWave> doInBackground(Void... voids) {
            List<LocalWave> waveList = LocalWaveDao.findByLikeName(name);
            return waveList;
        }

        @Override
        protected void onPostExecute(List<LocalWave> localWaves) {
            suggestListView.setVisibility(View.VISIBLE);
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(ShowViewActivity.this,android.R.layout.simple_list_item_1);
            for(LocalWave localWave:localWaves){
                arrayAdapter.add(localWave.getName());
            }
            suggestListView.setAdapter(arrayAdapter);
            suggestListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Utils.toggleSoftInput(ShowViewActivity.this);
                    for(SearchListener listener:observerList){
                        TextView textView = (TextView) view;
                        String text = (String) textView.getText();
                        //searchAutoComplete.setText(text);
                        mSearchView.clearFocus();
                        listener.onItemClick(witchTab,text);
                    }

                }
            });
        }
    }
    class ServerSuggestTask extends AsyncTask<Void,Integer,List<ServerWave>> {
        private String name;

        public ServerSuggestTask(String name) {
            this.name = name;
        }

        @Override
        protected List<ServerWave> doInBackground(Void... voids) {
            List<ServerWave> waveList = ServerWaveDao.findByLikeName(name);
            return waveList;
        }

        @Override
        protected void onPostExecute(List<ServerWave> localWaves) {
            suggestListView.setVisibility(View.VISIBLE);
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(ShowViewActivity.this,android.R.layout.simple_list_item_1);
            for(ServerWave serverWave:localWaves){
                arrayAdapter.add(serverWave.getName());
            }
            suggestListView.setAdapter(arrayAdapter);
            suggestListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Utils.toggleSoftInput(ShowViewActivity.this);
                    for(SearchListener listener:observerList){
                        TextView textView = (TextView) view;
                        String text = (String) textView.getText();
                        //searchAutoComplete.setText(text);

                        mSearchView.clearFocus();
                        listener.onItemClick(witchTab,text);
                    }

                }
            });
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.delete_all) {
            LocalWaveDao.deleteAll();
            LocalWavePointDao.deleteAll();
        } else if (itemId == R.id.add_server_data) {
            //addDataToServer();
        }else if(itemId == android.R.id.home){
            drawerLayout.openDrawer(GravityCompat.START);
        }
        return true;
    }

    @Override
    public void addObserver(SearchListener observer){
        observerList.add(observer);
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
                Toast.makeText(ShowViewActivity.this,"链接成功",Toast.LENGTH_LONG).show();
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


    private void displayData(byte[] buf) {
        Log.e("+++++++++++++",StringUtil.bytesToString(buf));

        Log.e("+++++++++++++------",buf.length+"");
        if (buf.length==16){
            byte[] messageTypeByte = new byte[1];
            messageTypeByte[0] = buf[1];

//            mData.append(StringUtil.bytesToString(buf)+"\n");
//
//            if ((StringUtil.bytesToString(messageTypeByte).replace(" ", "").equals("00") )) {
//                mData.append("链接成功");
//            }
//
//            if ((StringUtil.bytesToString(messageTypeByte).replace(" ", "").equals("02") )) {
//                mData.append("链接断开");
//            }
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
                    Toast.makeText(ShowViewActivity.this,"数据传输完毕",Toast.LENGTH_LONG).show();
                }

                LocalWavePoint localWavePoint = new LocalWavePoint();
                localWavePoint.setId(pointList.size()+1);
                localWavePoint.setPressUnit(1);
                localWavePoint.setWaveId(pointList.size()+1);
                localWavePoint.setX(Float.parseFloat(xCoordinateString));
                localWavePoint.setY(Float.parseFloat(yDecimalString));
                pointList.add(localWavePoint);

                pointSurfaceView.upload(pointList);

//                mData.append("第"+indexString+"位采集点，X轴坐标 ："+xSymbolString+xCoordinateString+
//                        ",Y轴坐标 ："+ySymbolString+yDecimalString+"\n\n");
            }
        }
//        else{
//            return;
//            Toast.makeText(ShowReceiveActivity.this,"数据异常",Toast.LENGTH_SHORT).show();
//        }
//        mDataRecvText.setText(mData.toString());
//        mRecvBytes.setText(recvBytes + " ");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_CODE_PHONE_STATE:
                boolean denied = false;
                for(int grant:grantResults){
                    if(grant != PackageManager.PERMISSION_GRANTED){
                        denied = true;
                    }
                }
                if(!denied){
                    init();
                }else{
                    ToastUtils.toast(ShowViewActivity.this,"请开启读取手机权限");
                    finish();
                }
                break;
        }
    }
}
