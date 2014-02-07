package com.vivavu.dream.adapter.main;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.vivavu.dream.fragment.MainContentsFragment;
import com.vivavu.dream.fragment.MainPlanFragment;

/**
 * Created by yuja on 14. 2. 5.
 */
public class MainPagerAdapter extends FragmentPagerAdapter {
    private Context mContext;
    private static final int MAX_MAIN_PAGER_COUNT = 2;

    public MainPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int i) {
        switch(i){
            case 0:
                return new MainContentsFragment();
            case 1:
                return new MainPlanFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return MAX_MAIN_PAGER_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch(position){
            case 0:
                return "Life";
            case 1:
                return "Plan";
        }
        return null;
    }
}
