package com.vivavu.dream.activity.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.vivavu.dream.R;
import com.vivavu.dream.activity.bucket.BucketAddActivity;
import com.vivavu.dream.activity.bucket.BucketViewActivity;
import com.vivavu.dream.adapter.main.MainPagerAdapter;
import com.vivavu.dream.common.BaseActionBarActivity;
import com.vivavu.dream.common.Code;
import com.vivavu.dream.fragment.CustomBaseFragment;
import com.vivavu.dream.fragment.main.MainContentsFragment;
import com.vivavu.dream.fragment.main.MainPlanFragment;
import com.vivavu.dream.repository.DataRepository;

public class MainActivity extends BaseActionBarActivity implements CustomBaseFragment.OnOptionFragmentRemovedListener{
    private MainContentsFragment mainContentsFragment;
    private MainPlanFragment mainPlanFragment;
    private MainPagerAdapter mMainPagerAdapter;
    private ViewPager mViewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*mMainPagerAdapter = new MainPagerAdapter(this, getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.main_view_pager);
        mViewPager.setAdapter(mMainPagerAdapter);*/

        if (savedInstanceState == null) {

            mainContentsFragment = new MainContentsFragment();
            mainPlanFragment = new MainPlanFragment();

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.content_frame, mainContentsFragment)
                    .commit();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        // Activity 와 Fragment 실행순서에 따라서 Fragment UI가 다 생성된 이후에 Activity에서
        // Fragment의 UI에 접근 가능. Activity.onCreate -> Fragment.onCreate->Activity.onStart가 수행

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
        getMenuInflater().inflate(R.menu.main_activity_actions, menu);
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
                refreshFragment();

                return true;
            case R.id.main_menu_plan:
                goPlan();
                return true;
            case R.id.main_menu_life:
                goLife();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void refreshFragment() {
        mainContentsFragment.refreshList(DataRepository.getBuckets(context.getUser().getId()));
        mainPlanFragment.refreshList(DataRepository.getPlanList(context.getUsername()));
    }

    private void goPlan() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, mainPlanFragment)
                .commit();
        mainPlanFragment.refreshList(DataRepository.getPlanList(context.getUsername()));
    }

    private void goLife(){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, mainContentsFragment)
                .commit();
        mainContentsFragment.refreshList(DataRepository.getBuckets(context.getUser().getId()));
    }

    private void goAddBucket() {
        Intent intent;
        intent = new Intent();
        intent.setClass(this, BucketAddActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onOptionFragmentRemoved(String tag) {

    }

    public void goBucketView(Integer bucketId){
        Intent intent = new Intent();
        intent.setClass(this, BucketViewActivity.class);
        intent.putExtra("bucketId", bucketId);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        //다시 활성화 될때.
        super.onResume();
        if (context.checkLogin() == false) {
            goLogin();
        }else{
            mainContentsFragment.setBuckets(DataRepository.getBuckets(context.getUser().getId()));
        }
    }

    @Override
    public void onBackPressed() {
        exit();
    }
}
