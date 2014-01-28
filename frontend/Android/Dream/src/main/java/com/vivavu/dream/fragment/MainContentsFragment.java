package com.vivavu.dream.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.vivavu.dream.R;
import com.vivavu.dream.adapter.bucket.BucketAdapter;
import com.vivavu.dream.common.CustomBaseFragment;
import com.vivavu.dream.model.bucket.Bucket;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yuja on 14. 1. 23.
 */
public class MainContentsFragment extends CustomBaseFragment {
    @InjectView(R.id.bucketList)
    ListView mBucketList;
    List<Bucket> buckets;
    private BucketAdapter bucketAdapter;

    public MainContentsFragment() {
        this.buckets = new ArrayList<Bucket>();
    }

    public MainContentsFragment(List<Bucket> buckets) {
        this.buckets = buckets;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshList(buckets);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.inject(this, rootView);
        bucketAdapter = new BucketAdapter(getActivity(), R.layout.bucket_list_item, buckets);
        mBucketList.setAdapter(bucketAdapter);
        return rootView;
    }

    public void refreshList(List<Bucket> buckets){
        if(context != null){
            bucketAdapter.setBuckets(buckets);
            bucketAdapter.notifyDataSetChanged();
        }
    }

    public List<Bucket> getBuckets() {
        return bucketAdapter.getBuckets();
    }

    public void setBuckets(List<Bucket> buckets) {
        this.buckets = buckets;
    }
}
