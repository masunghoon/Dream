package com.vivavu.dream.activity.bucket;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.vivavu.dream.R;
import com.vivavu.dream.common.BaseActionBarActivity;
import com.vivavu.dream.fragment.bucket.BucketOptionDDayFragment;
import com.vivavu.dream.fragment.bucket.BucketOptionDescriptionFragment;
import com.vivavu.dream.fragment.bucket.BucketOptionRepeatFragment;
import com.vivavu.dream.fragment.bucket.OptionBaseFragment;
import com.vivavu.dream.model.bucket.option.OptionDDay;
import com.vivavu.dream.model.bucket.option.OptionDescription;
import com.vivavu.dream.model.bucket.option.OptionRepeat;

/**
 * Created by yuja on 14. 3. 14.
 */
public class BucketOptionActivity extends BaseActionBarActivity {
    OptionBaseFragment bucketOption;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bucket_option_template);
        Intent data = getIntent();

        Object initData = data.getSerializableExtra("option");
        if(initData instanceof OptionDDay){
            bucketOption = new BucketOptionDDayFragment((OptionDDay) initData);
        }else if(initData instanceof OptionDescription){
            bucketOption = new BucketOptionDescriptionFragment((OptionDescription) initData);
        }else if(initData instanceof OptionRepeat){
            bucketOption = new BucketOptionRepeatFragment((OptionRepeat) initData);
        }else{
            finish();
        }

        getSupportFragmentManager().beginTransaction()
                .add(R.id.content_frame, bucketOption)
                .commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

}
