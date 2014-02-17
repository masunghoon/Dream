package com.vivavu.dream.common;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

import com.facebook.Session;
import com.vivavu.dream.activity.StartActivity;
import com.vivavu.dream.activity.intro.IntroActivity;
import com.vivavu.dream.activity.login.LoginActivity;
import com.vivavu.dream.activity.main.MainActivity;
import com.vivavu.dream.facebook.callback.CustomFacebookStatusCallback;
import com.vivavu.dream.model.BaseInfo;
import com.vivavu.dream.model.ResponseBodyWrapped;
import com.vivavu.dream.repository.DataRepository;
import com.vivavu.dream.util.AndroidUtils;

/**
 * Created by yuja on 14. 2. 6.
 */
public class BaseActionBarActivity extends ActionBarActivity implements View.OnClickListener{
    protected DreamApp context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = (DreamApp) getApplicationContext();
    }

    public DreamApp getContext() {
        return context;
    }

    public void setContext(DreamApp context) {
        this.context = context;
    }

    @Override
    public void onClick(View view) {
        AndroidUtils.autoVisibleSoftInputFromWindow(view);
    }

    @Override
    protected void onStart() {
        super.onStart();
        View view = AndroidUtils.getRootView(this);
        view.setOnClickListener(this);//root view에 click listener를 달아 두어 다른곳을 선책하면 키보드가 없어지도록 함
    }

    public void goLogin(){
        Intent intent = new Intent();
        intent.setClass(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivityForResult(intent, Code.ACT_LOGIN);
    }

    public void goIntro(){
        Intent intent = new Intent();
        intent.setClass(this, IntroActivity.class);
        startActivityForResult(intent, Code.ACT_INTRO);
    }

    public void goMain(){
        Intent intent = new Intent();
        intent.setClass(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(intent, Code.ACT_MAIN);
    }

    public void checkAppExit() {
        Intent intent = getIntent();
        boolean isAppExit = intent.getBooleanExtra("isAppExit", false);

        if(isAppExit){
            finish();
        }else{
            if(checkLogin()){
                goMain();
            }else{
                goIntro();
            }
        }
    }

    public boolean checkLogin(){
        if(context.isLogin() == false){
            if(context.hasValidToken()){
            ResponseBodyWrapped<BaseInfo> response = DataRepository.getBaseInfo();
                if(response.isSuccess()){
                    BaseInfo baseInfo = response.getData();
                    context.setUser(baseInfo);
                    context.setUsername(baseInfo.getUsername());
                    context.setLogin(true);
                    return true;
                }
            }
            context.setLogin(false);
            return false;
        }
        return true;
    }

    public void logout(){
        getContext().logout();

        Session session = Session.getActiveSession();
        if(session != null){
            //todo: close만 할것인지 clear 시켜서 토큰정보도 안남게 할 것인지.
            session.closeAndClearTokenInformation();
        }

        Intent intent = new Intent();
        intent.setClass(this, StartActivity.class);
        // activity 외부에서 activity 실행시 FLAG_ACTIVITY_NEW_TASK 를 넣어주어야한다.
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("isAppExit", false);
        startActivity(intent);
    }

    public void exit(){
        Intent intent = new Intent();
        intent.setClass(this, StartActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("isAppExit", true);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case Session.DEFAULT_AUTHORIZE_ACTIVITY_CODE:
                Session activeSession = Session.getActiveSession();
                if(activeSession != null){
                    activeSession.onActivityResult(this, requestCode, resultCode, data);
                    activeSession.addCallback(new CustomFacebookStatusCallback(this));
                }

                return;
        }

    }

    public class CheckLoginTesk extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            checkLogin();
            return null;
        }
    }
}
