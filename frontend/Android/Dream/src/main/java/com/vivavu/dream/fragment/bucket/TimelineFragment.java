package com.vivavu.dream.fragment.bucket;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.vivavu.dream.R;
import com.vivavu.dream.activity.bucket.timeline.TimelineItemEditActivity;
import com.vivavu.dream.fragment.CustomBaseFragment;
import com.vivavu.dream.model.bucket.Bucket;
import com.vivavu.dream.util.DateUtils;

import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yuja on 2014-03-27.
 */
public class TimelineFragment extends CustomBaseFragment {
    public static final String TAG = "com.vivavu.dream.fragment.bucket.TimelineFragment";
    @InjectView(R.id.progressBar)
    ProgressBar mProgressBar;
    @InjectView(R.id.txt_start_date)
    TextView mTxtStartDate;
    @InjectView(R.id.txt_current_date)
    TextView mTxtCurrentDate;
    @InjectView(R.id.txt_end_date)
    TextView mTxtEndDate;
    @InjectView(R.id.daily_timeline)
    ViewPager mDailyTimeline;
    @InjectView(R.id.container_bucket_info)
    LinearLayout mContainerBucketInfo;
    @InjectView(R.id.btn_add_timeline)
    Button mBtnAddTimeline;

    private Bucket bucket;

    public TimelineFragment(Bucket bucket) {
        this.bucket = bucket;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_timeline, container, false);
        ButterKnife.inject(this, rootView);

        buinData(bucket);
        initEvent();
        return rootView;
    }

    private void initEvent() {
        mBtnAddTimeline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TimelineItemEditActivity.class);
                startActivityForResult(intent, 1);
            }
        });
    }

    private void buinData(Bucket bucket) {
        Date start = bucket.getRegDate();
        Date current = new Date();
        Date end = bucket.getDeadline();
        mTxtStartDate.setText(DateUtils.getDateString(start, "yyyy.MM.dd"));
        mTxtCurrentDate.setText(DateUtils.getDateString(current, "yyyy.MM.dd"));
        mTxtEndDate.setText(DateUtils.getDateString(end, "yyyy.MM.dd"));

        if(start != null && end != null) {
            int progress = DateUtils.getProgress(start, end);
            mProgressBar.setProgress(progress);
        }


    }
}
