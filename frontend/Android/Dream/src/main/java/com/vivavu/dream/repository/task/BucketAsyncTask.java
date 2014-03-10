package com.vivavu.dream.repository.task;

import com.vivavu.dream.common.DreamApp;
import com.vivavu.dream.model.ResponseBodyWrapped;
import com.vivavu.dream.model.bucket.Bucket;
import com.vivavu.dream.repository.Connector;
import com.vivavu.dream.repository.DataRepository;

import java.util.List;

/**
 * Created by yuja on 14. 3. 6.
 */
public class BucketAsyncTask extends CustomAsyncTask<Void, Void, ResponseBodyWrapped<List<Bucket>>>  {

    private DreamApp context;

    public BucketAsyncTask(DreamApp context){
        this.context = context;
    }

    @Override
    protected ResponseBodyWrapped<List<Bucket>> doInBackground(Void... params) {
        Connector connector = new Connector();
        ResponseBodyWrapped<List<Bucket>> result = connector.getBucketList();
        return result;
    }

    public DreamApp getContext() {
        return context;
    }

    public void setContext(DreamApp context) {
        this.context = context;
    }

    @Override
    protected void onPostExecute(ResponseBodyWrapped<List<Bucket>> listResponseBodyWrapped) {
        DataRepository.saveBuckets(listResponseBodyWrapped.getData());
        onPostExecuteCallback.onPostExecuteCallback();//위에것을 하고 이 문장을 실행시켜야함
    }
}
