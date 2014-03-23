package com.vivavu.dream.fragment.main;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.vivavu.dream.R;
import com.vivavu.dream.adapter.bucket.BucketAdapter;
import com.vivavu.dream.fragment.CustomPullToRefreshFragment;
import com.vivavu.dream.model.ResponseBodyWrapped;
import com.vivavu.dream.model.bucket.Bucket;
import com.vivavu.dream.model.bucket.BucketGroup;
import com.vivavu.dream.repository.Connector;
import com.vivavu.dream.repository.DataRepository;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by yuja on 14. 2. 27.
 */
public class MainBucketListFragment extends CustomPullToRefreshFragment<ListView> {
    private static final int SEND_NETWORK_DATA = 3;
    public static String TAG = "com.vivavu.dream.fragment.main.MainBucketListFragment";
    private List<BucketGroup> bucketGroupList;
    private BucketAdapter bucketAdapter;
    public MainBucketListFragment() {
        bucketGroupList = new ArrayList<BucketGroup>();
    }

    protected final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case SEND_REFRESH_START:
                    mList.setRefreshing();
                    break;
                case SEND_REFRESH_STOP:
                    updateContents();
                    mList.onRefreshComplete();
                    break;
                case SEND_BUKET_LIST_UPDATE:
                    bucketGroupList.clear();
                    bucketGroupList.addAll((List<BucketGroup>) msg.obj);
                    updateContents();
                    mList.onRefreshComplete();
                    break;
                case SEND_NETWORK_DATA:
                    break;
            }
        }
    };

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
    }

    public void updateContents(){
        if(bucketAdapter == null){
            bucketAdapter = new BucketAdapter(getActivity(), R.layout.shelf_row, bucketGroupList);
            bucketAdapter.setParentFragment(this);
            mList.setAdapter(bucketAdapter);
        }
        bucketAdapter.setBucketList(bucketGroupList);
        bucketAdapter.notifyDataSetChanged();
        //mList.invalidate();
    }

    private void startUpdateData(){

    }

    @Override
    public void onResume() {
        super.onResume();
        Thread thread = new Thread(new DataThread());
        thread.start();
    }

    @Override
    public void onRefresh(final PullToRefreshBase<ListView> listViewPullToRefreshBase) {
        Thread thread = new Thread(new NetworkThread());
        thread.start();
    }


    public class NetworkThread implements Runnable{
        @Override
        public void run() {
            handler.sendEmptyMessage(SEND_REFRESH_START);
            Connector connector = new Connector();
            ResponseBodyWrapped<List<Bucket>> result = connector.getBucketList();
            if(result != null) {
                DataRepository.saveBuckets(result.getData());
            }

            handler.post(new DataThread());

        }
    }
    public class DataThread implements Runnable {
        @Override
        public void run() {
            List<BucketGroup> bucketGroup = DataRepository.listBucketGroup();
            Message message = handler.obtainMessage(SEND_BUKET_LIST_UPDATE, bucketGroup);
            handler.sendMessage(message);
        }
    }
}
