package com.vivavu.dream.fragment.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vivavu.dream.R;
import com.vivavu.dream.adapter.bucket.BucketAdapter;
import com.vivavu.dream.fragment.CustomListFragment;
import com.vivavu.dream.model.bucket.BucketGroup;
import com.vivavu.dream.repository.DataRepository;
import com.vivavu.dream.repository.task.BucketAsyncTask;
import com.vivavu.dream.repository.task.CustomAsyncTask;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by yuja on 14. 2. 27.
 */
public class MainBucketListFragment extends CustomListFragment {
    private List<BucketGroup> bucketList;
    private BucketAdapter bucketAdapter;
    public MainBucketListFragment() {
        bucketList = new ArrayList<BucketGroup>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_shelf_list, container, false);
        ButterKnife.inject(this, rootView);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        List<BucketGroup> bucketGroup = DataRepository.listBucketGroup();
        bucketList = bucketGroup;

        bucketAdapter = new BucketAdapter(getActivity(), R.layout.shelf_row, bucketList);

        bucketAdapter.setParentFragment(this);
        setListAdapter(bucketAdapter);

        BucketAsyncTask bucketAsyncTask = new BucketAsyncTask(getContext());
        bucketAsyncTask.setOnPostExecuteCallback(new CustomAsyncTask.OnPostExecuteCallback() {
            @Override
            public void onPostExecuteCallback() {
                updateContents();
            }
        });
        bucketAsyncTask.execute();
    }

    public void updateContents(){
        List<BucketGroup> bucketGroup = DataRepository.listBucketGroup();
        bucketList = bucketGroup;
        if(bucketAdapter == null){
            bucketAdapter = new BucketAdapter(getActivity(), R.layout.shelf_row, bucketList);
            bucketAdapter.setParentFragment(this);
            setListAdapter(bucketAdapter);
        }
        bucketAdapter.setBucketList(bucketList);
        //bucketAdapter.notifyDataSetChanged();//순서가 섞인다.
        getListView().invalidate();
    }

}
