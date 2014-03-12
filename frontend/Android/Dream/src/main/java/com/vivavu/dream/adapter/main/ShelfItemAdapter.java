package com.vivavu.dream.adapter.main;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.vivavu.dream.R;
import com.vivavu.dream.activity.bucket.BucketAddActivity;
import com.vivavu.dream.model.bucket.Bucket;
import com.vivavu.dream.util.DateUtils;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yuja on 14. 2. 27.
 */
public class ShelfItemAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater mInflater;
    private List<Bucket> bucketList;

    public ShelfItemAdapter(Context context) {
        this.context = context;
        this.mInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public ShelfItemAdapter(Context context, List<Bucket> bucketList) {
        this.context = context;
        this.mInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.bucketList = bucketList;
    }

    @Override
    public int getCount() {
        if(bucketList != null){
            return bucketList.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {

        return bucketList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ButterknifeViewHolder holder = null;
        if(convertView == null) {
            ViewGroup viewGroup = (ViewGroup) mInflater.inflate(R.layout.shelf_item, parent, false);
            holder  = new ButterknifeViewHolder(viewGroup);
            viewGroup.setTag(holder);
            convertView = viewGroup;

            viewGroup.setBackgroundColor(Color.argb(127, 0, 0, 255));
        }else{
            holder = (ButterknifeViewHolder) convertView.getTag();
        }

        init(holder, (Bucket) getItem(position));
        return convertView;
    }

    public void init(ButterknifeViewHolder holder, Bucket bucket) {

        holder.mBookTitle.setText(bucket.getTitle());
        holder.mBookDudate.setText(DateUtils.getDateString(bucket.getDeadline(), "yyyy-MM-dd"));
        holder.mBtnBookWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent();
                intent.setClass(context, BucketAddActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                context.startActivity(intent);
            }
        });
        /*
        holder.mBtnBookSetting.setOnClickListener(this);

        popupView = getActivity().getLayoutInflater().inflate(R.layout.book_setting_menu, null);
        PopupWindowViewHolder holder = new PopupWindowViewHolder(popupView);
        popupView.setTag(holder);
        holder.mBtnBucketStatusAchievement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.hide();
            }
        });
        holder.mBtnBucketStatusSeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.hide();
            }
        });
        holder.mBtnBucketStatusDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.hide();
            }
        });
        mPopupWindow = AndroidUtils.makePopupWindow(popupView);
        mPopupWindow.setAnimationStyle(-1); // 애니메이션 설정(-1:설정, 0:설정안함)*/
    }
/**
 * This class contains all butterknife-injected Views & Layouts from layout file 'null'
 * for easy to all layout elements.
 *
 * @author Android Butter Zelezny, plugin for IntelliJ IDEA/Android Studio by Inmite (www.inmite.eu)
 */
    class ButterknifeViewHolder {
        @InjectView(R.id.btn_book_write)
        Button mBtnBookWrite;
        @InjectView(R.id.btn_book_setting)
        Button mBtnBookSetting;
        @InjectView(R.id.book_title)
        TextView mBookTitle;
        @InjectView(R.id.book_dudate)
        TextView mBookDudate;
        @InjectView(R.id.book_status)
        TextView mBookStatus;

        ButterknifeViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
