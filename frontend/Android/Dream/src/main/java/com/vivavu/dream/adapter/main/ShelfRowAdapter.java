package com.vivavu.dream.adapter.main;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vivavu.dream.R;

/**
 * Created by yuja on 14. 2. 27.
 */
public class ShelfRowAdapter extends PagerAdapter {
    private Context context;
    private LayoutInflater mInflater;

    public ShelfRowAdapter(Context context) {
        this.context = context;
        this.mInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ViewGroup viewGroup = (ViewGroup) mInflater.inflate(R.layout.shelf_item, container, false);

        container.addView(viewGroup);
        return viewGroup;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
    }

    @Override
    public int getCount() {
        return 5;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == object);
    }
}
