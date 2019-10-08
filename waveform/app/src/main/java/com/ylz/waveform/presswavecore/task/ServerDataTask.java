package com.ylz.waveform.presswavecore.task;

import android.os.AsyncTask;

import com.ylz.waveform.presswavecore.Config;
import com.ylz.waveform.presswavecore.callback.OnLoadFinishCallback;
import com.ylz.waveform.presswavecore.dao.ServerWaveDao;
import com.ylz.waveform.presswavecore.enums.SearchPreciseEnum;
import com.ylz.waveform.presswavecore.model.Page;
import com.ylz.waveform.presswavecore.model.ServerVo;
import com.ylz.waveform.presswavecore.model.db.ServerWave;
import com.ylz.waveform.presswavecore.utils.ConvertUtils;

import java.util.List;

public class ServerDataTask {

    private String name;

    private int searchPreciseKey;//搜索精确度：1：模糊搜索，2：精确搜索

    private int pageNo;

    public ServerDataTask(String name, int searchPreciseKey) {
        this.name = name;
        this.searchPreciseKey = searchPreciseKey;
    }

    public void execute(int pageNo){
        this.pageNo = pageNo;
        new BeforeTask().execute();
    }
    class BeforeTask extends AsyncTask<Void,Integer, List<ServerVo>>{
        private Page page;
        @Override
        protected List<ServerVo> doInBackground(Void... voids) {
            page = new Page(Config.SERVER_DATA_PAGE_SIZE,pageNo);

            List<ServerWave> serverWaveList;
            if(SearchPreciseEnum.FUZZY.getKey() == searchPreciseKey){
                serverWaveList = ServerWaveDao.findByLikePage(name,page);
            }else{
                serverWaveList = ServerWaveDao.findByPage(name,page);
            }
            List<ServerVo> serverVos = ConvertUtils.toServerVoList(serverWaveList);
            return serverVos;
        }

        @Override
        protected void onPostExecute(List<ServerVo> serverVos) {
            if(null != serverVos){
                int size = serverVos.size();
                if(size < Config.SERVER_DATA_PAGE_SIZE){
                    if(null != onLoadFinishCallback)
                        onLoadFinishCallback.noMore(serverVos);
                }else{
                    if(null != onLoadFinishCallback)
                        onLoadFinishCallback.hasMore(serverVos);
                }
            }
        }
    }
    private OnLoadFinishCallback onLoadFinishCallback;

    public void setOnLoadFinishCallback(OnLoadFinishCallback onLoadFinishCallback) {
        this.onLoadFinishCallback = onLoadFinishCallback;
    }
}
