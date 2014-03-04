package com.vivavu.dream.adapter.main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.vivavu.dream.fragment.main.MainShelfItemFragment;
import com.vivavu.dream.model.bucket.Bucket;

import java.util.List;

/**
 * Created by yuja on 14. 2. 27.
 */
public class ShelfRowFragmentAdapter extends FragmentStatePagerAdapter {
    private List<Bucket> bucketList;

    public ShelfRowFragmentAdapter(FragmentManager fm, List<Bucket> bucketList) {
        super(fm);
        this.bucketList = bucketList;
    }

    @Override
    public Fragment getItem(int position) {
        return MainShelfItemFragment.newInstance(bucketList.get(position));
    }

    @Override
    public int getCount() {
        return bucketList.size()    ;
    }
}
