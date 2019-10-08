package com.ylz.waveform.activity.presswave.localService;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.ylz.waveform.activity.presswave.model.db.LocalWavePoint;

import java.util.List;

public class BlueService extends Service {
    private static final String TAG = "BlueService";
    private static final String ACTION_BLUETOOTH_POINT = "com.ylz.waveform.activity.presswave.ACTION_BLUETOOTH_POINT";
    private BlueReceiver blueReceiver = new BlueReceiver();
    private LocalBinder localBinder = new LocalBinder();

    public BlueService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter intentFilter = new IntentFilter(ACTION_BLUETOOTH_POINT);
        registerReceiver(blueReceiver,intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(blueReceiver);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return localBinder;
    }

    public class LocalBinder extends Binder{

        public BlueService getService(){
            return BlueService.this;
        }
    }
    class BlueReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
            String json = intent.getStringExtra("bluetooth_point_json");
            List<LocalWavePoint> localWavePointList
                    = gson.fromJson(json,new TypeToken<List<LocalWavePoint>>(){}.getType());
            if(null != blueCallback){
                blueCallback.onShowPoints(localWavePointList);
            }
        }
    }
    public interface BlueCallback{
        void onShowPoints(List<LocalWavePoint> pointList);
    }
    private BlueCallback blueCallback;

    public void setBlueCallback(BlueCallback blueCallback) {
        this.blueCallback = blueCallback;
    }
}
