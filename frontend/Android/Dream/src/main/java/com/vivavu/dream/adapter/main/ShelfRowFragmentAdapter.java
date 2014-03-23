package com.vivavu.dream.adapter.main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.vivavu.dream.fragment.main.MainShelfItemFragment;
import com.vivavu.dream.model.bucket.Bucket;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuja on 14. 2. 27.
 */
public class ShelfRowFragmentAdapter extends FragmentStatePagerAdapter {
    private List<Bucket> bucketList;

    public ShelfRowFragmentAdapter(FragmentManager fm) {
        super(fm);
        this.bucketList = new ArrayList<Bucket>() ;
    }

    public ShelfRowFragmentAdapter(FragmentManager fm, List<Bucket> bucketList) {
        super(fm);
        this.bucketList = new ArrayList<Bucket>(bucketList) ;
    }

    @Override
    public Fragment getItem(int position) {
        return MainShelfItemFragment.newInstance(bucketList.get(position));
    }

    @Override
    public int getCount() {
        if(bucketList.size() > 0 ){
            Log.v("dream", bucketList.get(0).toString());
        }
        Log.v("dream", String.valueOf(bucketList.size()));
        return bucketList.size()    ;
    }

    public List<Bucket> getBucketList() {
        return bucketList;
    }

    public void setBucketList(List<Bucket> bucketList) {
        this.bucketList.clear();
        this.bucketList.addAll(bucketList);
        //super.notifyDataSetChanged();
    }

    public void refreshDataSet(List<Bucket> bucketList) {
        this.bucketList.clear();
        this.bucketList.addAll(bucketList);
        notifyDataSetChanged();
        super.notifyDataSetChanged();
    }
}
