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
import com.vivavu.dream.R;
import com.vivavu.dream.adapter.bucket.TodayRowAdapter;
import com.vivavu.dream.fragment.CustomPullToRefreshFragment;
import com.vivavu.dream.model.bucket.TodayGroup;
import com.vivavu.dream.repository.DataRepository;
import com.vivavu.dream.repository.task.CustomAsyncTask;
import com.vivavu.dream.repository.task.TodayAsyncTask;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by yuja on 14. 1. 23.
 */
public class MainTodayFragment extends CustomPullToRefreshFragment<ListView>{
    public static String TAG = "com.vivavu.dream.fragment.main.MainTodayFragment";
    private List<TodayGroup> todayList;
    private TodayRowAdapter todayRowAdapter;

    protected final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case SEND_REFRESH_START:
                    mList.setRefreshing();
                    break;
                case SEND_REFRESH_STOP:
                case SEND_BUKET_LIST_UPDATE:
                    updateContents();
                    mList.onRefreshComplete();
                    break;
            }
        }
    };

    public MainTodayFragment() {
        this(new ArrayList<TodayGroup>());
    }

    public MainTodayFragment(List<TodayGroup> todayList) {
        this.todayList = todayList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_shelf_list, container, false);
        ButterKnife.inject(this, rootView);
        mList.setOnRefreshListener(this);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        handler.sendEmptyMessage(SEND_REFRESH_START);
    }

    @Override
    protected void updateContents() {
        if(todayRowAdapter == null){
            todayRowAdapter = new TodayRowAdapter(getActivity(), todayList);
            mList.setAdapter(todayRowAdapter);
        }
        todayRowAdapter.setTodayGroupList(todayList);
        todayRowAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh(final PullToRefreshBase<ListView> listViewPullToRefreshBase) {
        TodayAsyncTask todayAsyncTask = new TodayAsyncTask(getContext());
        todayAsyncTask.setOnPostExecuteCallback(new CustomAsyncTask.OnPostExecuteCallback() {
            @Override
            public void onPostExecuteCallback() {
                DataAsync dataAsync = new DataAsync();
                dataAsync.execute();
            }
        });
        todayAsyncTask.execute();
    }

    public class DataAsync extends AsyncTask<Void, Void, List<TodayGroup>> {

        @Override
        protected List<TodayGroup> doInBackground(Void... params) {
            List<TodayGroup> todayGroup = DataRepository.listTodayGroup();
            return todayGroup;
        }

        @Override
        protected void onPostExecute(List<TodayGroup> todayGroupList) {
            super.onPostExecute(todayGroupList);
            todayList = todayGroupList;
            handler.sendEmptyMessage(SEND_REFRESH_STOP);
        }
    }
}
