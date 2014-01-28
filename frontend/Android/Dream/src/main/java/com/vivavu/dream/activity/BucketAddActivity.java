package com.vivavu.dream.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.vivavu.dream.R;
import com.vivavu.dream.common.Code;
import com.vivavu.dream.common.DreamApp;
import com.vivavu.dream.common.Scope;
import com.vivavu.dream.model.bucket.Bucket;
import com.vivavu.dream.model.bucket.Dday;
import com.vivavu.dream.repository.DataRepository;
import com.vivavu.dream.util.DateUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yuja on 14. 1. 13.
 */
public class BucketAddActivity extends ActionBarActivity implements View.OnClickListener {
    @InjectView(R.id.bucket_input_title)
    EditText mBucketInputTitle;
    @InjectView(R.id.btn_input_dday)
    Button mBtnInputDday;
    @InjectView(R.id.text_input_dday)
    TextView mTextInputDday;
    @InjectView(R.id.text_input_remain)
    TextView mTextInputRemain;
    @InjectView(R.id.btn_range_in_my_life)
    Button mBtnRangeInMyLife;
    @InjectView(R.id.btn_range_custom)
    Button mBtnRangeCustom;
    @InjectView(R.id.layout_dday_option)
    LinearLayout mLayoutDdayOption;
    @InjectView(R.id.btn_custom_date_set)
    Button mBtnCustomDateSet;
    @InjectView(R.id.custom_date)
    DatePicker mCustomDate;
    @InjectView(R.id.layout_bucket_add_custom_date)
    LinearLayout mLayoutBucketAddCustomDate;
    @InjectView(R.id.btn_range_1)
    Button mBtnRange1;
    @InjectView(R.id.btn_range_2)
    Button mBtnRange2;
    @InjectView(R.id.btn_range_3)
    Button mBtnRange3;
    @InjectView(R.id.btn_range_4)
    Button mBtnRange4;
    @InjectView(R.id.btn_range_5)
    Button mBtnRange5;
    @InjectView(R.id.btn_range_6)
    Button mBtnRange6;
    private LayoutInflater layoutInflater;
    private Bucket bucket = null;
    private DreamApp context = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bucket_input_template);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        context = (DreamApp) getApplicationContext();
        Intent data = getIntent();

        int bucketId = data.getIntExtra("bucketId", -1);
        int parentId = data.getIntExtra("parentId", -1);
        Bucket parentBucket = (Bucket) data.getSerializableExtra("parentBucket");

        int code;
        if (bucketId > 0) {
            bucket = DataRepository.getBucket(bucketId);
            code = Code.ACT_MOD_BUCKET_DEFAULT_CARD;
            actionBar.setTitle("Mod Bucket");
        } else {
            bucket = new Bucket("", null);
            code = Code.ACT_ADD_BUCKET_DEFAULT_CARD;
            actionBar.setTitle("Add Bucket");
        }

        if(parentBucket != null){
            bucket.setLevel(parentBucket.getLevel() + 1);
            bucket.setParentId(parentBucket.getId());
            bucket.setScope(Scope.PLAN.getValue());
        }

        ScrollView root = (ScrollView) findViewById(R.id.view_intput_template);
        layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // intent에서 넘겨준 인자에 따라서 기본입력인지 옵션 입력인지 분리하여 UI 구성시킴

        View viewGroup = layoutInflater.inflate(R.layout.bucket_input_default, null, false);
        ButterKnife.inject(this, viewGroup);
        root.addView(viewGroup);

        mLayoutDdayOption.setVisibility(LinearLayout.GONE);
        mBtnInputDday.setOnClickListener(this);

        addEventListener();
        List<Dday> list = DateUtils.getUserDdays(DateUtils.getDateFromString(context.getUser().getBirthday(), "yyyyMMdd", Calendar.getInstance().getTime()));
        makeDdaysButtonUi(list);

        switch (code) {
            case Code.ACT_ADD_BUCKET_DEFAULT_CARD:

                break;
            case Code.ACT_MOD_BUCKET_DEFAULT_CARD:


                bindData();
                break;
        }

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
                if (bucket != null && bucket.getTitle() != null && bucket.getTitle().length() > 0) {
                    Bucket result = null;
                    if (bucket.getId() != null && bucket.getId() > 0) {
                        result = DataRepository.updateBucketInfo(bucket);
                        if (result != null) {
                            Toast.makeText(this, "수정 성공", Toast.LENGTH_LONG).show();
                            setResult(RESULT_OK);
                            finish();
                        }

                    } else {
                        result = DataRepository.postBucketDefault(bucket, context.getUsername());


                        if (result != null) {
                            Toast.makeText(this, "등록 성공", Toast.LENGTH_LONG).show();
                            intent = new Intent(this, BucketViewActivity.class);
                            intent.putExtra("bucketId", (Integer) result.getId());
                            startActivity(intent);
                        }
                    }
                }else {
                    Toast.makeText(this, "필수입력 항목이 입력되지 않았음", Toast.LENGTH_SHORT).show();
                }

                return true;
            case R.id.bucket_add_menu_cancel:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void addEventListener() {
        //기한선택
        mBtnRange1.setOnClickListener(this);
        mBtnRange2.setOnClickListener(this);
        mBtnRange3.setOnClickListener(this);
        mBtnRange4.setOnClickListener(this);
        mBtnRange5.setOnClickListener(this);
        mBtnRange6.setOnClickListener(this);
        mBtnRangeInMyLife.setOnClickListener(this);
        mBtnRangeCustom.setOnClickListener(this);

        //커스텀 기한선택
        mBtnCustomDateSet.setOnClickListener(this);

        mBucketInputTitle.addTextChangedListener(textWatcherInput);



    }

    private void updateUiData(Dday dday) {
        if (dday.getDeadline() != null) {
            mTextInputDday.setText(dday.getDdayString());
        } else {
            mTextInputDday.setText(dday.getRange());
        }
        if (dday.getDeadline() != null) {
            mTextInputRemain.setText("remain " + DateUtils.getRemainDay(dday.getDeadline()).toString() + " Days");
        } else {
            mTextInputRemain.setText("");
        }
        bucket.setTitle(mBucketInputTitle.getText().toString());
        bucket.setDeadline(dday.getDeadline());
    }

    private void bindData() {
        mTextInputDday.setText(bucket.getRange());
        mTextInputRemain.setText("remain " + DateUtils.getRemainDay(bucket.getDeadline()).toString() + " Days");
        mBucketInputTitle.setText(bucket.getTitle());
    }

    private List<Button> makeDdayButton(List<Dday> ddays) {
        List<Button> buttons = new ArrayList<Button>();
        for (int i = 0; i < ddays.size(); i++) {
            Button button = new Button(this);
            button.setOnClickListener(this);
            button.setTag(ddays.get(i));
            button.setText(ddays.get(i).getRange());
            buttons.add(button);

        }
        return buttons;
    }

    private void makeDdaysButtonUi(List<Dday> ddays) {

        if( ddays.size() == 6){
            mBtnRange1.setTag( ddays.get(0));
            mBtnRange2.setTag( ddays.get(1));
            mBtnRange3.setTag( ddays.get(2));
            mBtnRange4.setTag( ddays.get(3));
            mBtnRange5.setTag( ddays.get(4));
            mBtnRange6.setTag(ddays.get(5));
        }

        return;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_input_dday) {
            if(mLayoutDdayOption.getVisibility() == LinearLayout.GONE){
                mLayoutDdayOption.setVisibility(LinearLayout.VISIBLE);
            }else{
                mLayoutDdayOption.setVisibility(LinearLayout.GONE);
            }
        } else if (view.getTag() != null && view.getTag() instanceof Dday) {
            Dday dday = (Dday) view.getTag();
            updateUiData(dday);
            mLayoutDdayOption.setVisibility(LinearLayout.GONE);
        } else if (view.getId() == R.id.btn_range_in_my_life) {
            Dday dday = new Dday(getString(R.string.txt_btn_range_in_my_life), null);
            updateUiData(dday);
            mLayoutDdayOption.setVisibility(LinearLayout.GONE);
        } else if (view.getId() == R.id.btn_range_custom) {
            mLayoutBucketAddCustomDate.setVisibility(View.VISIBLE);
            mLayoutDdayOption.setVisibility(LinearLayout.GONE);
        } else if (view.getId() == R.id.btn_custom_date_set) {
            mLayoutBucketAddCustomDate.setVisibility(View.GONE);
            //mLayoutDdayOption.setVisibility(LinearLayout.VISIBLE);
            mLayoutDdayOption.setVisibility(LinearLayout.GONE);
            Date selectedDate = DateUtils.getDate(mCustomDate.getYear(), mCustomDate.getMonth(), mCustomDate.getDayOfMonth());
            Dday dday = new Dday("custom", selectedDate);

            updateUiData(dday);
        }
    }

    TextWatcher textWatcherInput = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            bucket.setTitle(s.toString());
        }
    };
}
