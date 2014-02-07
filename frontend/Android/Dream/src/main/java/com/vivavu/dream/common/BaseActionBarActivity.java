package com.vivavu.dream.common;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

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
}
