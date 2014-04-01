package com.vivavu.dream.activity.bucket.timeline;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.vivavu.dream.R;
import com.vivavu.dream.activity.bucket.TimelineActivity;
import com.vivavu.dream.common.BaseActionBarActivity;
import com.vivavu.dream.fragment.bucket.timeline.TimelineItemEditFragment;
import com.vivavu.dream.model.ResponseBodyWrapped;
import com.vivavu.dream.model.bucket.Bucket;
import com.vivavu.dream.model.bucket.timeline.Post;
import com.vivavu.dream.repository.TimelineConnector;
import com.vivavu.dream.util.DateUtils;

import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static android.widget.Toast.LENGTH_LONG;

/**
 * Created by yuja on 2014-03-28.
 */
public class TimelineItemEditActivity extends BaseActionBarActivity {
    public static final String TAG = "com.vivavu.dream.activity.bucket.timeline.TimelineItemEditActivity";

    @InjectView(R.id.btn_timeline_title)
    Button mBtnTimelineTitle;
    @InjectView(R.id.progressBar)
    ProgressBar mProgressBar;
    @InjectView(R.id.txt_start_date)
    TextView mTxtStartDate;
    @InjectView(R.id.txt_current_date)
    TextView mTxtCurrentDate;
    @InjectView(R.id.txt_end_date)
    TextView mTxtEndDate;
    @InjectView(R.id.container_post_info)
    LinearLayout mContainerBucketInfo;
    @InjectView(R.id.content_frame)
    LinearLayout mContentFrame;

    private ProgressDialog progressDialog;
    private static final int SEND_DATA_START = 0;
    private static final int SEND_DATA_SUCCESS = 1;
    private static final int SEND_DATA_FAIL = 2;

    protected final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case SEND_DATA_START:
                    progressDialog.show();
                    break;
                case SEND_DATA_SUCCESS:
                    progressDialog.dismiss();
                    Toast.makeText(TimelineItemEditActivity.this, "저장하였습니다.", LENGTH_LONG).show();
                    setResult(RESULT_OK);
                    finish();
                    break;
                case SEND_DATA_FAIL:
                    progressDialog.dismiss();
                    Toast.makeText(TimelineItemEditActivity.this, "저장에 실패하였습니다.", LENGTH_LONG).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline_item);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.actionbar_timeline_title_only);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("진행중");

        ButterKnife.inject(this);

        Intent data = getIntent();
        Bucket bucket = (Bucket) data.getSerializableExtra(TimelineActivity.extraKeyBucket);
        Post post  = (Post) data.getSerializableExtra(TimelineActivity.extraKeyPost);
        post.setBucketId(bucket.getId());

        bindData(bucket);

        TimelineItemEditFragment timelineItemEditFragment = new TimelineItemEditFragment(post);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.content_frame , timelineItemEditFragment, TimelineItemEditFragment.TAG)
                .commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.timeline_item_edit_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_save:
                postSave();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void postSave() {
        TimelineItemEditFragment fragmentByTag = (TimelineItemEditFragment) getSupportFragmentManager().findFragmentByTag(TimelineItemEditFragment.TAG);
        if(fragmentByTag != null ){
            Post post = fragmentByTag.getPost();
            NetworkThread networkThread = new NetworkThread(post);
            Thread thread = new Thread(networkThread);
            thread.start();
        }
    }

    private void bindData(Bucket bucket) {
        mBtnTimelineTitle.setText(bucket.getTitle());
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

    private class NetworkThread implements Runnable{
        protected Post post;

        public NetworkThread(Post post) {
            this.post = post;
        }

        @Override
        public void run() {
            handler.sendEmptyMessage(SEND_DATA_START);

            TimelineConnector timelineConnector = new TimelineConnector();
            ResponseBodyWrapped<Post> result = timelineConnector.post(post);
            if(result.isSuccess()) {
                handler.sendEmptyMessage(SEND_DATA_SUCCESS);
            }else {
                handler.sendEmptyMessage(SEND_DATA_FAIL);
            }
        }
    }

}
