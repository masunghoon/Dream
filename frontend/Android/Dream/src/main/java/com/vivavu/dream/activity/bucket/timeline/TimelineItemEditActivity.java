package com.vivavu.dream.activity.bucket.timeline;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;

import com.vivavu.dream.R;
import com.vivavu.dream.common.BaseActionBarActivity;

import butterknife.ButterKnife;

/**
 * Created by yuja on 2014-03-28.
 */
public class TimelineItemEditActivity extends BaseActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.actionbar_timeline_title_only);

        ButterKnife.inject(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.timeline_item_edit_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
