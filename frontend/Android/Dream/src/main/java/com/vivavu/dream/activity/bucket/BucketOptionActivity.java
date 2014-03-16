package com.vivavu.dream.activity.bucket;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.vivavu.dream.R;
import com.vivavu.dream.common.BaseActionBarActivity;
import com.vivavu.dream.fragment.bucket.option.dday.DDayFragment;
import com.vivavu.dream.fragment.bucket.option.description.DescriptionFragment;
import com.vivavu.dream.fragment.bucket.option.repeat.RepeatFragment;
import com.vivavu.dream.fragment.bucket.option.OptionBaseFragment;
import com.vivavu.dream.model.bucket.option.Option;
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
            bucketOption = new DDayFragment((OptionDDay) initData);
        }else if(initData instanceof OptionDescription){
            bucketOption = new DescriptionFragment((OptionDescription) initData);
        }else if(initData instanceof OptionRepeat){
            bucketOption = new RepeatFragment((OptionRepeat) initData);
        }else{
            finish();
        }

        getSupportFragmentManager().beginTransaction()
                .add(R.id.content_frame, bucketOption)
                .commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bucket_add_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent;
        switch (id) {
            case R.id.bucket_add_menu_save:
                Option option = bucketOption.getContents();
                intent = new Intent();
                intent.putExtra("option.result", option);
                setResult(Activity.RESULT_OK, intent);
                finish();

                return true;
            case R.id.bucket_add_menu_cancel:
                setResult(Activity.RESULT_CANCELED);
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
