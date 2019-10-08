package com.ylz.waveform.presswavecore.task;

import android.os.AsyncTask;

import com.ylz.waveform.presswavecore.Config;
import com.ylz.waveform.presswavecore.callback.OnLoadFinishCallback;
import com.ylz.waveform.presswavecore.dao.LocalWaveDao;
import com.ylz.waveform.presswavecore.enums.SearchPreciseEnum;
import com.ylz.waveform.presswavecore.model.LocalVo;
import com.ylz.waveform.presswavecore.model.Page;
import com.ylz.waveform.presswavecore.model.db.LocalWave;
import com.ylz.waveform.presswavecore.utils.ConvertUtils;

import java.util.List;

public class LocalDataTask {

    private String name;

    private int pageNo;

    private int searchPreciseKey;//搜索精确度：1：模糊搜索，2：精确搜索

    public LocalDataTask(String name, int searchPreciseKey) {
        this.name = name;
        this.searchPreciseKey = searchPreciseKey;
    }

    public void execute(int pageNo){
        this.pageNo = pageNo;
        new BeforeTask().execute();
    }

    class BeforeTask extends AsyncTask<Void,Integer, List<LocalVo>>{

        @Override
        protected List<LocalVo> doInBackground(Void... voids) {
            Page page = new Page(Config.LOCAL_DATA_PAGE_SIZE,pageNo);
            List<LocalWave> localWaveList ;
            if(SearchPreciseEnum.FUZZY.getKey() == searchPreciseKey){
                localWaveList = LocalWaveDao.findByLikePage(name,page);
            }else{
                localWaveList = LocalWaveDao.findByPage(name,page);
            }

            List<LocalVo> localVos = ConvertUtils.toLocalVoList(localWaveList);
            return localVos;
        }

        @Override
        protected void onPostExecute(List<LocalVo> localVos) {
            if(null != localVos){
                int size = localVos.size();
                if(size < Config.LOCAL_DATA_PAGE_SIZE){
                    if(null != onLoadFinishCallback)
                        onLoadFinishCallback.noMore(localVos);
                }else{
                    if(null != onLoadFinishCallback)
                        onLoadFinishCallback.hasMore(localVos);
                }
            }
        }
    }
    private OnLoadFinishCallback onLoadFinishCallback;

    public void setOnLoadFinishCallback(OnLoadFinishCallback onLoadFinishCallback) {
        this.onLoadFinishCallback = onLoadFinishCallback;
    }
}
