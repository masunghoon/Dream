package com.vivavu.dream.activity.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.vivavu.dream.R;
import com.vivavu.dream.activity.bucket.BucketAddActivity;
import com.vivavu.dream.activity.bucket.BucketViewActivity;
import com.vivavu.dream.common.BaseActionBarActivity;
import com.vivavu.dream.common.Code;
import com.vivavu.dream.fragment.main.MainTodayDailyFragment;
import com.vivavu.dream.fragment.main.MainTodayFragment;
import com.vivavu.dream.util.AndroidUtils;
import com.vivavu.dream.view.ButtonIncludeCount;
import com.vivavu.dream.view.CustomPopupWindow;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yuja on 2014-03-21.
 */
public class TodayActivity extends BaseActionBarActivity {
    @InjectView(R.id.btn_add_bucket)
    Button mBtnAddBucket;
    @InjectView(R.id.actionbar_main_title)
    TextView mActionbarMainTitle;
    @InjectView(R.id.actionbar_main_notice)
    Button mActionbarMainNotice;
    @InjectView(R.id.actionbar_main_today)
    ButtonIncludeCount mActionbarMainToday;

    View noticeView;
    CustomPopupWindow mPopupNotice;

    MainTodayFragment mainTodayFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //actionbar setting
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.actionbar_main);

        ButterKnife.inject(this);

        if (savedInstanceState == null) {
            mainTodayFragment= new MainTodayFragment();
            MainTodayDailyFragment mainTodayDailyFragment = new MainTodayDailyFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.content_frame, mainTodayDailyFragment, mainTodayDailyFragment.TAG)
                    .commit();
        }

        noticeView = getLayoutInflater().inflate(R.layout.actionbar_notice, null);
        mPopupNotice = AndroidUtils.makePopupWindow(noticeView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        //mPopupNotice.setAnimationStyle(R.style.AnimationPopup);
        mBtnAddBucket.setOnClickListener(this);
        mActionbarMainNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mPopupNotice != null && !mPopupNotice.isShowing() && !v.isSelected()){
                    mPopupNotice.showAsDropDown(v);
                    v.setSelected(true);
                }else{
                    mPopupNotice.hide();
                    v.setSelected(false);
                }
            }
        });

        mActionbarMainToday.getButton().setText("Main");
        mActionbarMainToday.getTextView().setText("1");
        mActionbarMainToday.getButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case Code.ACT_ADD_BUCKET:
                int bucketId = data.getIntExtra("bucketId", -1);
                if (bucketId > 0) {
                    goBucketView(bucketId);
                }

                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //actionbar에서 모든걸 처리
        //getMenuInflater().inflate(R.menu.main_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Intent intent;
        switch (id) {
            case R.id.main_menu_add_bucket:
                goAddBucket();
                return true;
            case R.id.main_menu_refresh_bucket_list:

                return true;
            case R.id.main_menu_plan:
                return true;
            case R.id.main_menu_life:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.btn_add_bucket:
                goAddBucket();
                break;
        }
    }

    private void goAddBucket() {
        Intent intent;
        intent = new Intent();
        intent.setClass(this, BucketAddActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void goBucketView(Integer bucketId) {
        Intent intent = new Intent();
        intent.setClass(this, BucketViewActivity.class);
        intent.putExtra("bucketId", bucketId);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        //다시 활성화 될때.
        super.onResume();
        if (checkLogin() == false) {
            //goLogin();
        } else {

        }
    }

    @Override
    public void onBackPressed() {
        if(mPopupNotice != null && mPopupNotice.isShowing()){
            mPopupNotice.hide();
        }else{
            finish();
        }
    }
}
