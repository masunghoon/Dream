package com.vivavu.dream.activity.bucket;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.widget.Button;
import android.widget.LinearLayout;

import com.vivavu.dream.R;
import com.vivavu.dream.common.BaseActionBarActivity;
import com.vivavu.dream.fragment.bucket.TimelineFragment;
import com.vivavu.dream.model.bucket.Bucket;
import com.vivavu.dream.repository.DataRepository;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yuja on 2014-03-27.
 */
public class TimelineActivity extends BaseActionBarActivity {
    public static final String TAG = "com.vivavu.dream.activity.bucket.TimelineActivity";
    public static final String extraKey = "bucketId";
    TimelineFragment timelineFragment;
    @InjectView(R.id.btn_timeline_title)
    Button mBtnTimelineTitle;
    @InjectView(R.id.btn_bucket_complete)
    Button mBtnBucketComplete;
    @InjectView(R.id.content_frame)
    LinearLayout mContentFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.actionbar_timeline);

        ButterKnife.inject(this);

        Intent data = getIntent();
        Integer bucketId = data.getIntExtra(extraKey, -1);
        Bucket bucket = DataRepository.getBucket(bucketId);
        bindData(bucket);
        if (savedInstanceState == null) {
            timelineFragment = new TimelineFragment(bucket);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.content_frame, timelineFragment, timelineFragment.TAG)
                    .commit();
        }
    }

    private void bindData(Bucket bucket) {
        mBtnTimelineTitle.setText(bucket.getTitle());
    }
}
