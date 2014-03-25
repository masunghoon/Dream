package com.vivavu.dream.adapter.today;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;

import com.vivavu.dream.R;
import com.vivavu.dream.model.bucket.TodayGroup;
import com.vivavu.dream.util.DateUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yuja on 14. 2. 27.
 */
public class TodayDailyViewAdapter extends PagerAdapter implements View.OnClickListener{

    private Context context;
    private LayoutInflater mInflater;
    private List<TodayGroup> todayGroupList;

    public TodayDailyViewAdapter(Context context, List<TodayGroup> todayGroupList) {
        this.context = context;
        this.mInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.todayGroupList = new ArrayList<TodayGroup>(todayGroupList);
    }

    public TodayDailyViewAdapter(Context context) {
        this.context = context;
        this.mInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.todayGroupList = new ArrayList<TodayGroup>();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ViewGroup viewGroup = (ViewGroup) mInflater.inflate(R.layout.today_daily_view, container, false);
        ButterknifeViewHolder holder = new ButterknifeViewHolder(viewGroup);
        viewGroup.setTag(holder);

        init(holder, todayGroupList.get(position));
        container.addView(viewGroup);
        return viewGroup;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
    }

    @Override
    public int getCount() {
        return todayGroupList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == object);
    }

    public void init(ButterknifeViewHolder holder, TodayGroup todayGroup) {
        holder.mBtnDate.setText(DateUtils.getDateString(todayGroup.getDate(), "yyyy-MM-dd"));
        holder.mTodayContents.setAdapter(new TodayDailyItemAdapter(context, todayGroup.getTodayList()));
    }

    public List<TodayGroup> getTodayGroupList() {
        return todayGroupList;
    }

    public void setTodayGroupList(List<TodayGroup> todayGroupList) {
        this.todayGroupList = todayGroupList;
    }

    @Override
    public void onClick(View v) {

    }

    class ButterknifeViewHolder {
        @InjectView(R.id.btn_date)
        Button mBtnDate;
        @InjectView(R.id.today_contents)
        GridView mTodayContents;

        ButterknifeViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
