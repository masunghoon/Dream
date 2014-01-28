package com.vivavu.dream;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.vivavu.dream.activity.BucketAddActivity;
import com.vivavu.dream.activity.BucketViewActivity;
import com.vivavu.dream.common.Code;
import com.vivavu.dream.common.Constants;
import com.vivavu.dream.common.CustomBaseFragment;
import com.vivavu.dream.common.DreamApp;
import com.vivavu.dream.fragment.MainContentsFragment;
import com.vivavu.dream.fragment.MainPlanFragment;
import com.vivavu.dream.model.BaseInfo;
import com.vivavu.dream.repository.DataRepository;

public class MainActivity extends ActionBarActivity implements CustomBaseFragment.OnOptionFragmentRemovedListener{
    private DreamApp context = null;
    private MainContentsFragment mainContentsFragment;
    private MainPlanFragment mainPlanFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = (DreamApp) getApplicationContext();
        loadAppDefaultInfo();

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
        if (checkLogin() == false) {
            goLogin();
            return;
        }
        mainContentsFragment.setBuckets(DataRepository.getBucketsV2(context.getUsername()));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case Code.ACT_LOGIN:
                if (resultCode == RESULT_OK) {
                    mainContentsFragment.refreshList(DataRepository.getBucketsV2(context.getUsername()));
                }

                break;
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
            case R.id.main_menu_login:
                goLogin();
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
        mainContentsFragment.refreshList(DataRepository.getBucketsV2(context.getUsername()));
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
        mainContentsFragment.refreshList(DataRepository.getBucketsV2(context.getUsername()));
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
        saveAppDefaultInfo();
        super.onStop();
    }

    private boolean checkLogin() {
        BaseInfo baseInfo = DataRepository.getBaseInfo();
        if (baseInfo != null) {
            context.setUser(baseInfo);
            context.setUsername(baseInfo.getUsername());
            context.setLogin(true);
            return true;
        }
        context.setLogin(false);
        return false;
    }

    private void loadAppDefaultInfo() {
        if (context == null) {
            context = (DreamApp) getApplicationContext();
        }
        //기존 저장된 로그인 관련 정보 불러오기
        SharedPreferences settings = getSharedPreferences(Constants.settings, MODE_PRIVATE);
        String email = settings.getString(Constants.email, "");
        String token = settings.getString(Constants.token, "");

        context.setEmail(email);
        context.setToken(token);
    }

    private void saveAppDefaultInfo() {
        if (context == null) {
            context = (DreamApp) getApplicationContext();
        }

        // 프리퍼런스 설정
        SharedPreferences prefs = getSharedPreferences(Constants.settings, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(Constants.email, context.getEmail());
        if (context.getToken() != null) {
            editor.putString(Constants.token, context.getToken());   // String
        }
        editor.commit();
    }

    @Override
    public void onOptionFragmentRemoved(String tag) {

    }

    public void goLogin(){
        Intent intent = new Intent();
        intent.setClass(this, LoginActivity.class);
        startActivityForResult(intent, Code.ACT_LOGIN);
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
    }
}
