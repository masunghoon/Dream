package com.vivavu.dream.fragment.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vivavu.dream.R;
import com.vivavu.dream.adapter.bucket.BucketAdapter;
import com.vivavu.dream.fragment.CustomListFragment;
import com.vivavu.dream.model.bucket.Bucket;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by yuja on 14. 2. 27.
 */
public class MainBucketListFragment extends CustomListFragment {
    private List<Bucket> bucketList = new ArrayList<Bucket>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_shelf_list, container, false);
        ButterKnife.inject(this, rootView);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        bucketList.add(new Bucket());
        bucketList.add(new Bucket());
        bucketList.add(new Bucket());
        bucketList.add(new Bucket());
        bucketList.add(new Bucket());
        bucketList.add(new Bucket());
        BucketAdapter bucketAdapter = new BucketAdapter(getActivity(), R.layout.shelf_row, bucketList);
        bucketAdapter.setParentFragment(this);
        setListAdapter(bucketAdapter);

    }
}
