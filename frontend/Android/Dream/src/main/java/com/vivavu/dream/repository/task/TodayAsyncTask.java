package com.vivavu.dream.repository.task;

import com.vivavu.dream.common.DreamApp;
import com.vivavu.dream.model.ResponseBodyWrapped;
import com.vivavu.dream.model.bucket.Today;
import com.vivavu.dream.repository.Connector;
import com.vivavu.dream.repository.DataRepository;

import java.util.List;

/**
 * Created by yuja on 14. 3. 6.
 */
public class TodayAsyncTask extends CustomAsyncTask<Void, Void, ResponseBodyWrapped<List<Today>>>  {

    private DreamApp context;

    public TodayAsyncTask(DreamApp context){
        this.context = context;
    }

    @Override
    protected ResponseBodyWrapped<List<Today>> doInBackground(Void... params) {
        Connector connector = new Connector();
        ResponseBodyWrapped<List<Today>> result = connector.getTodayList(null);
        return result;
    }

    public DreamApp getContext() {
        return context;
    }

    public void setContext(DreamApp context) {
        this.context = context;
    }

    @Override
    protected void onPostExecute(ResponseBodyWrapped<List<Today>> listResponseBodyWrapped) {
        if(listResponseBodyWrapped != null) {
            DataRepository.saveTodays(listResponseBodyWrapped.getData());
        }
        if(onPostExecuteCallback != null) {
            onPostExecuteCallback.onPostExecuteCallback();//위에것을 하고 이 문장을 실행시켜야함
        }
    }
}
