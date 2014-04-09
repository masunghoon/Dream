package com.vivavu.dream.fragment.main;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.vivavu.dream.R;
import com.vivavu.dream.adapter.bucket.BucketAdapter;
import com.vivavu.dream.common.DreamApp;
import com.vivavu.dream.fragment.CustomBaseFragment;
import com.vivavu.dream.model.ResponseBodyWrapped;
import com.vivavu.dream.model.bucket.Bucket;
import com.vivavu.dream.model.bucket.BucketGroup;
import com.vivavu.dream.repository.BucketConnector;
import com.vivavu.dream.repository.DataRepository;
import com.vivavu.dream.util.NetworkUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yuja on 14. 2. 27.
 */
public class MainBucketListFragment extends CustomBaseFragment implements PullToRefreshListView.OnRefreshListener<ListView> {
    static public String TAG = "com.vivavu.dream.fragment.main.MainBucketListFragment";
    static public final int SEND_REFRESH_START = 0;
    static public final int SEND_REFRESH_STOP = 1;
    static public final int SEND_BUKET_LIST_UPDATE = 2;
    private static final int SEND_NETWORK_DATA = 3;

    @InjectView(R.id.list)
    protected PullToRefreshListView mList;
    private ProgressDialog progressDialog;

    protected final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case SEND_REFRESH_START:
                    progressDialog.show();
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
                    progressDialog.dismiss();
                    break;
                case SEND_NETWORK_DATA:
                    break;
            }
        }
    };

    private List<BucketGroup> bucketGroupList;
    private BucketAdapter bucketAdapter;
    public MainBucketListFragment() {
        bucketGroupList = new ArrayList<BucketGroup>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_shelf_list, container, false);
        ButterKnife.inject(this, rootView);
        mList.setOnRefreshListener(this);
        /*mList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // Pause fetcher to ensure smoother scrolling when flinging
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
                    // Before Honeycomb pause image loading on scroll to help with performance
                    if (!Utils.hasHoneycomb()) {
                    }
                } else {
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });*/
        //updateContents();
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("진행중");
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Thread thread = new Thread(new DataThread());
        thread.start();
    }

    public void updateContents(){
        if(bucketAdapter == null){
            bucketAdapter = new BucketAdapter(getActivity(), R.layout.shelf_row, bucketGroupList);
            bucketAdapter.setParentFragment(this);
            mList.setAdapter(bucketAdapter);
        }
        bucketAdapter.refreshDataSet(bucketGroupList);
        mList.invalidate();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(bucketAdapter != null) {
            bucketAdapter.notifyDataSetChanged();
        }
        /*Thread thread = new Thread(new DataThread());
        thread.start();*/
    }

    @Override
    public void onRefresh(final PullToRefreshBase<ListView> listViewPullToRefreshBase) {
        if(NetworkUtil.isAvaliableNetworkAccess(DreamApp.getInstance())) {
            Thread thread = new Thread(new NetworkThread());
            thread.start();
        }else {
            Toast.makeText(getActivity(), getText(R.string.no_network_connection_toast), Toast.LENGTH_SHORT).show();
            mList.onRefreshComplete();
        }
    }


    public class NetworkThread implements Runnable{
        @Override
        public void run() {
            handler.sendEmptyMessage(SEND_REFRESH_START);
            BucketConnector bucketConnector = new BucketConnector();
            ResponseBodyWrapped<List<Bucket>> result = bucketConnector.getBucketList();
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
