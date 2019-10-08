package com.ylz.waveform.activity.presswave;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.support.v7.app.AlertDialog;
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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ylz.waveform.R;
import com.ylz.waveform.presswavecore.Config;
import com.ylz.waveform.presswavecore.MyDataViewPagerAdapter;
import com.ylz.waveform.presswavecore.dao.LocalWaveDao;
import com.ylz.waveform.presswavecore.dao.LocalWavePointDao;
import com.ylz.waveform.presswavecore.dao.ServerWaveDao;
import com.ylz.waveform.presswavecore.enums.SearchPreciseEnum;
import com.ylz.waveform.presswavecore.enums.TabDataSourceEnum;
import com.ylz.waveform.presswavecore.listener.FragmentCallback;
import com.ylz.waveform.presswavecore.listener.SearchListener;
import com.ylz.waveform.presswavecore.localService.BlueService;
import com.ylz.waveform.presswavecore.model.ServerVo;
import com.ylz.waveform.presswavecore.model.db.LocalWave;
import com.ylz.waveform.presswavecore.model.db.LocalWavePoint;
import com.ylz.waveform.presswavecore.model.db.ServerWave;
import com.ylz.waveform.presswavecore.model.db.ServerWavePoint;
import com.ylz.waveform.presswavecore.utils.OkHttpUtil;
import com.ylz.waveform.presswavecore.utils.ToastUtils;
import com.ylz.waveform.presswavecore.utils.Utils;
import com.ylz.waveform.presswavecore.widget.BluePointViewWrap;
import com.ylz.waveform.authlogin.MyJump;
import com.ylz.waveform.authlogin.SharedPreferenceUtils;
import com.ylz.waveform.authlogin.User;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity  implements FragmentCallback {
    public static final String TAG = "MainActivity";
    private SharedPreferenceUtils sharedPreferenceUtils = new SharedPreferenceUtils(this);
    private BluePointViewWrap bluePointViewWrap;
    private ViewPager viewPager;
    private PagerTabStrip pagerTabStrip;
    private String name = "1";
    //默认模糊搜索
    private int searchPreciseKey = SearchPreciseEnum.FUZZY.getKey();
    private TabDataSourceEnum witchTab = TabDataSourceEnum.LOCAL_DATA;

    private SearchView mSearchView;
    private SearchView.SearchAutoComplete searchAutoComplete;
    private ListView suggestListView;
    private List<SearchListener> observerList = new ArrayList<>();

    private BlueServiceConnection connection = new BlueServiceConnection();
    private final static int REQUEST_CODE_PHONE_STATE = 1;
    private boolean isConnected;
    private Context context;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.context = getApplicationContext();
        int granted = ContextCompat.checkSelfPermission(this,"Manifest.permission.READ_PHONE_STATE");
        if(PackageManager.PERMISSION_GRANTED == granted){
            init();
        }else{
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_PHONE_STATE},REQUEST_CODE_PHONE_STATE);
        }
    }
    public void init(){
        // 使用Toolbar代替actionbar
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        suggestListView = findViewById(R.id.suggest_list_view);
        bluePointViewWrap = findViewById(R.id.blue_point_view_wrap);
        viewPager = findViewById(R.id.view_pager_data);
        pagerTabStrip = findViewById(R.id.pager_tab_strip);
        pagerTabStrip.setTabIndicatorColorResource(R.color.colorAccent);
        pagerTabStrip.setDrawFullUnderline(true);
        MyDataViewPagerAdapter myDataViewPagerAdapter
                = new MyDataViewPagerAdapter(getSupportFragmentManager()
                ,name);
        viewPager.setAdapter(myDataViewPagerAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                Log.i(TAG, "onPageSelected: "+i);
                witchTab = TabDataSourceEnum.getEnumByKey(i+1);
                //searchAutoComplete.setText("");
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        Intent intent = new Intent(this, BlueService.class);
        isConnected = bindService(intent,connection, Context.BIND_AUTO_CREATE);
        navigationView = findViewById(R.id.navigation_view);
        drawerLayout = findViewById(R.id.drawer_layout);
        User user = sharedPreferenceUtils.getUser();
        final String phoneNumber = user.phoneNumber;
        String userName = user.userName;
        View headView = navigationView.getHeaderView(0);
        TextView userNameTv = headView.findViewById(R.id.user_name);
        TextView phoneNumberTv = headView.findViewById(R.id.phone_number);
        userNameTv.setText(userName);
        phoneNumberTv.setText(phoneNumber);
        navigationView.setCheckedItem(R.id.btn_modify_password);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int itemId = menuItem.getItemId();
                if(R.id.btn_modify_password == itemId){
                    drawerLayout.closeDrawer(GravityCompat.START);
                    MyJump.jumpToModifyPassword(MainActivity.this,phoneNumber);
                }else if(R.id.btn_login_out == itemId){
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("确定退出吗");
                    builder.setPositiveButton("确定",new DialogInterface.OnClickListener(){

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String password = sharedPreferenceUtils.getPassword();
                            sharedPreferenceUtils.loginOut();
                            finish();
                            if("".equals(password)){
                                MyJump.jumpToSetPassword(MainActivity.this,phoneNumber);
                            }
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
                return true;
            }
        });

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
                Utils.toggleSoftInput(context);
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
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(MainActivity.this,android.R.layout.simple_list_item_1);
            for(LocalWave localWave:localWaves){
                arrayAdapter.add(localWave.getName());
            }
            suggestListView.setAdapter(arrayAdapter);
            suggestListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Utils.toggleSoftInput(context);
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
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(MainActivity.this,android.R.layout.simple_list_item_1);
            for(ServerWave serverWave:localWaves){
                arrayAdapter.add(serverWave.getName());
            }
            suggestListView.setAdapter(arrayAdapter);
            suggestListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Utils.toggleSoftInput(context);
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



    public void addDataToServer(){
        String url = Config.URL_ADD;
        ServerWave wave = new ServerWave();
        wave.setName("服务器数据1");
        wave.setUploadTime(new Date());
        wave.setVersionName("1.0");
        wave.setUserName("于哥");
        List<LocalWavePoint> localWavePointList = bluePointViewWrap.pointView.generatePointList();
        ServerVo serverVo = new ServerVo();
        List<ServerWavePoint> serverWavePoints = new ArrayList<>();
        for(LocalWavePoint localWavePoint:localWavePointList){
            ServerWavePoint serverWavePoint = new ServerWavePoint();
            serverWavePoint.setX(localWavePoint.getX());
            serverWavePoint.setY(localWavePoint.getY());
            serverWavePoint.setPressUnit(localWavePoint.getPressUnit());
            serverWavePoints.add(serverWavePoint);
        }
        serverVo.setServerWavePointList(serverWavePoints);
        serverVo.setServerWave(wave);
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        String json = gson.toJson(serverVo);
        OkHttpUtil.post(url, json, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onFailure: "+e.getMessage());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.toast(context,"增加失败");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.toast(context,"增加成功");
                    }
                });
            }
        });
    }

    @Override
    public void addObserver(SearchListener observer){
        observerList.add(observer);
    }

    class BlueServiceConnection implements ServiceConnection{

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            BlueService blueService = ((BlueService.LocalBinder)service).getService();
            blueService.setBlueCallback(bluePointViewWrap.pointView);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(isConnected){
            unbindService(connection);
        }
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
                    ToastUtils.toast(context,"请开启读取手机权限");
                    finish();
                }
                break;
        }
    }
}
