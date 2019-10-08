package com.ylz.waveform.activity.presswave.widget;

import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ylz.waveform.activity.presswave.PressWaveApplication;
import com.ylz.waveform.R;
import com.ylz.waveform.activity.presswave.dao.LocalWaveDao;
import com.ylz.waveform.activity.presswave.dao.LocalWavePointDao;
import com.ylz.waveform.activity.presswave.dialog.SavePointDialog;
import com.ylz.waveform.activity.presswave.model.LocalVo;
import com.ylz.waveform.activity.presswave.model.UserWaveVo;
import com.ylz.waveform.activity.presswave.model.db.LocalWave;
import com.ylz.waveform.activity.presswave.model.db.LocalWavePoint;
import com.ylz.waveform.activity.presswave.model.gson.GsonUtils;
import com.ylz.waveform.activity.presswave.utils.OkHttpUtil;
import com.ylz.waveform.activity.presswave.utils.ToastUtils;
import com.ylz.waveform.activity.presswave.utils.Utils;
import com.ylz.waveform.presswavecore.Config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class BluePointViewWrap extends LinearLayout implements SavePointDialog.SaveBtnClickListener {
    private static final String TAG = "BluePointViewWrap";
    public PointSurfaceView pointView;
    private SavePointDialog dialog;
    public BluePointViewWrap(Context context) {
        super(context);
    }

    public BluePointViewWrap(final Context context, AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(R.layout.blue_point_view_wrap,this);

         pointView = view.findViewById(R.id.point_surface_view);
//        for(int i=0;i<100;i++){
//            List<LocalWavePoint> localWavePointList = pointView.generatePointList();
//            pointView.upload(localWavePointList);
//        }
        Button savePointBtn = view.findViewById(R.id.save_point_btn);

        savePointBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new SavePointDialog(context);
                dialog.setSaveBtnClickListener(BluePointViewWrap.this);
                dialog.show();

            }
        });
        Button clearPointBtn = view.findViewById(R.id.clear_point_btn);
        clearPointBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                pointView.clearPoints();
            }
        });

    }

    public BluePointViewWrap(Context context,  AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onSaveBtnClick(String name,String remark) {
        LruCache<String,LocalWavePoint> lruCache = pointView.getLruCache();
        Map<String,LocalWavePoint> cache  = lruCache.snapshot();
        List<LocalWavePoint> localWavePointList = new ArrayList<>();
        for(String key:cache.keySet()){
            LocalWavePoint point = cache.get(key);
            LocalWavePoint localWavePoint = new LocalWavePoint();
            localWavePoint.setX(point.getX());
            localWavePoint.setY(point.getY());
            localWavePoint.setPressUnit(point.getPressUnit());
            localWavePointList.add(localWavePoint);
        }
        new SaveTask(name,remark,localWavePointList).execute();
    }
    class SaveTask extends AsyncTask<Void,Integer,String>{
        private String name;
        private String remark;
        private List<LocalWavePoint> localWavePointList;

        public SaveTask(String name,String remark, List<LocalWavePoint> localWavePointList) {
            this.name = name;
            this.remark = remark;
            this.localWavePointList = localWavePointList;
        }

        @Override
        protected String doInBackground(Void... voids) {
            LocalWave localWave = new LocalWave();
            localWave.setName(name);
            String phoneNumber = Utils.getPhoneNumber();
            localWave.setUserName(phoneNumber);
            localWave.setCollectTime(new Date());
            localWave.setRemark(remark);
            LocalWaveDao.save(localWave);
            int waveId = localWave.getId();
            for(LocalWavePoint point:localWavePointList){
                point.setWaveId(waveId);
            }
            try {
                LocalWavePointDao.saveBatch(localWavePointList);
                LocalVo localVo = new LocalVo();
                localVo.setLocalWavePointList(localWavePointList);
                localVo.setLocalWave(localWave);
                UserWaveVo userWaveVo = GsonUtils.convert(localVo);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                String json = gson.toJson(userWaveVo);
                String url = Config.URL_USER_WAVE_ADD;
                OkHttpUtil.post(url, json, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.i(TAG, "onFailure: "+e.getMessage());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Log.i(TAG, "onResponse: ");
                    }
                });
            }catch (Exception e){
                e.printStackTrace();
                return "保存失败";
            }

            return "保存成功";
        }

        @Override
        protected void onPostExecute(String s) {
            if("保存成功".equals(s)){
                dialog.dissmiss();
                if(null != saveCallback){
                    saveCallback.refresh();
                }
            }
            ToastUtils.toast(s);
        }
    }
    private static SaveCallback saveCallback;
    public interface SaveCallback{
        void refresh();
    }

    public static void setSaveCallback(SaveCallback callback) {
        saveCallback = callback;
    }
}
