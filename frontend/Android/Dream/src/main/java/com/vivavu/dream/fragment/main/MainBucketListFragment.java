package com.vivavu.dream.fragment.main;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.vivavu.dream.R;
import com.vivavu.dream.adapter.bucket.BucketAdapter;
import com.vivavu.dream.fragment.CustomBaseFragment;
import com.vivavu.dream.model.bucket.BucketGroup;
import com.vivavu.dream.repository.DataRepository;
import com.vivavu.dream.repository.task.BucketAsyncTask;
import com.vivavu.dream.repository.task.CustomAsyncTask;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yuja on 14. 2. 27.
 */
public class MainBucketListFragment extends CustomBaseFragment implements PullToRefreshListView.OnRefreshListener<ListView>{
    @InjectView(android.R.id.list)
    PullToRefreshListView mList;
    final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mList.setRefreshing();
            updateContents();
            mList.onRefreshComplete();
        }
    };

    private List<BucketGroup> bucketList;
    private BucketAdapter bucketAdapter;
    public MainBucketListFragment() {
        bucketList = new ArrayList<BucketGroup>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_shelf_list, container, false);
        ButterKnife.inject(this, rootView);
        mList.setOnRefreshListener(this);
        //updateContents();
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        DataAsync dataAsync = new DataAsync();
        dataAsync.execute();
    }

    public void updateContents(){
        List<BucketGroup> bucketGroup = DataRepository.listBucketGroup();
        bucketList = bucketGroup;
        if(bucketAdapter == null){
            bucketAdapter = new BucketAdapter(getActivity(), R.layout.shelf_row, bucketList);
            bucketAdapter.setParentFragment(this);
            mList.setAdapter(bucketAdapter);
        }
        bucketAdapter.setBucketList(bucketList);
        bucketAdapter.notifyDataSetChanged();//순서가 섞인다.
        //mList.invalidate();
    }

    @Override
    public void onRefresh(final PullToRefreshBase<ListView> listViewPullToRefreshBase) {
        BucketAsyncTask bucketAsyncTask = new BucketAsyncTask(getContext());
        bucketAsyncTask.setOnPostExecuteCallback(new CustomAsyncTask.OnPostExecuteCallback() {
            @Override
            public void onPostExecuteCallback() {
                updateContents();
                listViewPullToRefreshBase.onRefreshComplete();
            }
        });
        bucketAsyncTask.execute();

    }
    public class DataAsync extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... params) {
            Message message = handler.obtainMessage();
            handler.sendMessage(message);
            return null;
        }
    }
}
