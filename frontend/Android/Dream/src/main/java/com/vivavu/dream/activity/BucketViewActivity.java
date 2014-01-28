package com.vivavu.dream.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.vivavu.dream.R;
import com.vivavu.dream.common.Code;
import com.vivavu.dream.common.CustomBaseFragment;
import com.vivavu.dream.common.RepeatType;
import com.vivavu.dream.common.Tag;
import com.vivavu.dream.fragment.MainContentsFragment;
import com.vivavu.dream.fragment.bucket.BucketOptionReaptFragment;
import com.vivavu.dream.model.bucket.Bucket;
import com.vivavu.dream.model.bucket.option.OptionRepeat;
import com.vivavu.dream.repository.DataRepository;
import com.vivavu.dream.util.DateUtils;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yuja on 14. 1. 10.
 */
public class BucketViewActivity extends ActionBarActivity implements Button.OnClickListener, CustomBaseFragment.OnOptionFragmentRemovedListener {
    @InjectView(R.id.bucket_btn_done)
    Button mBucketBtnDone;
    @InjectView(R.id.bucket_item_title)
    TextView mBucketItemTitle;
    @InjectView(R.id.bucket_default_card_btn_dday)
    ImageView mBucketDefaultCardBtnDday;
    @InjectView(R.id.bucket_item_scope)
    TextView mBucketItemScope;
    @InjectView(R.id.bucket_item_remain)
    TextView mBucketItemRemain;
    @InjectView(R.id.bucket_item_progressbar)
    ProgressBar mBucketItemProgressbar;
    @InjectView(R.id.imageView)
    ImageView mImageView;
    @InjectView(R.id.bucket_option_note)
    EditText mBucketOptionNote;
    @InjectView(R.id.btn_option_remove)
    Button mBtnOptionRemove;
    @InjectView(R.id.layout_bucket_option_note)
    LinearLayout mLayoutBucketOptionNote;
    @InjectView(R.id.bucket_option_list)
    ScrollView mBucketOptionList;
    @InjectView(R.id.btn_bucket_option_note)
    Button mBtnBucketOptionNote;
    @InjectView(R.id.btn_bucket_option_public)
    Button mBtnBucketOptionPublic;
    @InjectView(R.id.btn_bucket_option_share)
    Button mBtnBucketOptionShare;
    @InjectView(R.id.btn_bucket_option_pic)
    Button mBtnBucketOptionPic;
    @InjectView(R.id.btn_bucket_option_del)
    Button mBtnBucketOptionDel;
    @InjectView(R.id.btn_bucket_option_plan)
    Button mBtnBucketOptionPlan;
    @InjectView(R.id.btn_bucket_option_repeat)
    Button mBtnBucketOptionRepeat;
    @InjectView(R.id.fragment_bucket_option_repeat)
    LinearLayout mFragmentBucketOptionRepeat;
    @InjectView(R.id.layout_subbucket_list)
    LinearLayout mLayoutSubbucketList;
    private Bucket bucket;

    private MainContentsFragment mainContentsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bucket_item_view);
        ButterKnife.inject(this);
        Integer bucketId = getIntent().getIntExtra("bucketId", -1);

        bindEventListener();

        bucket = null;
        if (bucketId > 0) {
            bucket = DataRepository.getBucketV2(bucketId);
            bindData();
        }
        Log.d("dream", bucket.toString());

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Code.ACT_MOD_BUCKET_DEFAULT_CARD:
                //데이터 새로고침
                bucket = DataRepository.getBucket(bucket.getId());
                bindData();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bucket_view_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent;
        switch (id) {
            case R.id.bucket_view_menu_save:
                getOptionData();
                if (DataRepository.updateBucketInfo(bucket) != null) {
                    Toast.makeText(this, "수정완료", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "수정실패", Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.bucket_view_menu_cancel:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bucket_item_title:
                Intent intent = new Intent(this, BucketAddActivity.class);
                intent.putExtra("bucketId", (Integer) bucket.getId());
                startActivityForResult(intent, Code.ACT_MOD_BUCKET_DEFAULT_CARD);
                break;
            case R.id.btn_bucket_option_note:
                mLayoutBucketOptionNote.setVisibility(View.VISIBLE);
                mBucketOptionList.setVisibility(View.VISIBLE);
                mBtnBucketOptionNote.setVisibility(View.GONE);

                break;
            case R.id.btn_bucket_option_public:
                view.setSelected(!view.isSelected());
                if (view.isSelected()) {
                    bucket.setIsPrivate(1);
                    Toast.makeText(this, "비공개", Toast.LENGTH_SHORT).show();
                } else {
                    bucket.setIsPrivate(0);
                    Toast.makeText(this, "공개", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.bucket_btn_done:
                view.setSelected(!view.isSelected());
                if (view.isSelected()) {
                    bucket.setIsLive(1);
                    Toast.makeText(this, "완료", Toast.LENGTH_SHORT).show();
                } else {
                    bucket.setIsLive(0);
                    Toast.makeText(this, "진행중", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_option_remove:
                bucket.setDescription(null);
                bindData();

                break;
            case R.id.btn_bucket_option_del:
                AlertDialog.Builder alert_confirm = new AlertDialog.Builder(this);
                alert_confirm.setMessage("삭제하시겠습니까?").setCancelable(false).setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 'YES' 제거 로직 추가
                                DataRepository.deleteBucket(bucket.getId());
                                Toast.makeText(BucketViewActivity.this, "삭제확인", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }).setNegativeButton("취소",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 'No'
                                Toast.makeText(BucketViewActivity.this, "삭제취소", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        });
                AlertDialog alert = alert_confirm.create();
                alert.show();
                break;
            case R.id.btn_bucket_option_share:
                Toast.makeText(this, "공유하기", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_bucket_option_plan:
                goAddBucket(bucket.getId());
                break;
            case R.id.btn_bucket_option_repeat:
                //Todo: 임시로 addOptionRepeat()을 위해 bucket에 값을 넣음, 초기에 option카드를 그릴지 말지 결정하는 다른 알고리즘 필요
                bucket.setRptType(RepeatType.WKRP.getCode());
                addOptionRepeat();
                break;

        }
    }

    private void bindEventListener() {
        mBucketBtnDone.setOnClickListener(this);
        mBucketItemTitle.setOnClickListener(this);
        mBtnBucketOptionNote.setOnClickListener(this);
        mBucketOptionNote.addTextChangedListener(textWatcherInput);
        mBtnBucketOptionPublic.setOnClickListener(this);
        mBtnOptionRemove.setOnClickListener(this);
        mBtnBucketOptionDel.setOnClickListener(this);
        mBtnBucketOptionPlan.setOnClickListener(this);
        mBtnBucketOptionRepeat.setOnClickListener(this);
    }

    private void bindData() {

        mBucketBtnDone.setSelected(bucket.getIsLive() == 1);

        mBucketItemTitle.setText(bucket.getTitle());
        mBucketItemTitle.setTag(bucket.getId());

        mBucketItemScope.setText(bucket.getRange());

        mBucketItemRemain.setText("remain " + DateUtils.getRemainDay(bucket.getDeadline()).toString() + " Days");

        if (bucket.getTodos() == null || bucket.getTodos().size() < 1) {
            mBucketItemProgressbar.setVisibility(ProgressBar.GONE);
        } else {
            mBucketItemProgressbar.setVisibility(ProgressBar.VISIBLE);
            mBucketItemProgressbar.setProgress(bucket.getProgress());
        }

        mBucketOptionList.setVisibility(View.VISIBLE);
        mBucketOptionNote.setText(bucket.getDescription());
        if (bucket.getDescription() != null && bucket.getDescription().length() > 0) {
            mLayoutBucketOptionNote.setVisibility(View.VISIBLE);
            mBtnBucketOptionNote.setVisibility(Button.GONE);
        } else {
            mLayoutBucketOptionNote.setVisibility(View.GONE);
            mBtnBucketOptionNote.setVisibility(Button.VISIBLE);
        }

        mBtnBucketOptionPublic.setSelected(bucket.getIsPrivate() == 1);

        addOptionRepeat();
        addSubBucketList(bucket.getSubBuckets());
    }

    private void getOptionData() {
        //옵션에서의 내용들 읽어오기
        // 반복설정
        BucketOptionReaptFragment repeatFragment = (BucketOptionReaptFragment) getSupportFragmentManager().findFragmentByTag(Tag.BUCKET_OPTION_FRAGMENT_REPEAT);
        if (repeatFragment != null) {
            OptionRepeat repeat = repeatFragment.getContents();
            bucket.setRptType(repeat.getRepeatType().getCode());
            bucket.setRptCndt(repeat.getOptionStat());
        } else {
            bucket.setRptType(null);
            bucket.setRptCndt(null);
        }
        //
    }

    private void addOptionRepeat() {

        BucketOptionReaptFragment bucketOptionReaptFragment = (BucketOptionReaptFragment) getSupportFragmentManager().findFragmentByTag(Tag.BUCKET_OPTION_FRAGMENT_REPEAT);
        if (bucketOptionReaptFragment == null) {
            if (bucket.getRptType() != null) {
                OptionRepeat optionRepeat = new OptionRepeat();
                optionRepeat.setRepeatType(RepeatType.fromCode(bucket.getRptType()));
                optionRepeat.setOptionStat(bucket.getRptCndt());
                bucketOptionReaptFragment = new BucketOptionReaptFragment(optionRepeat);
                getSupportFragmentManager().beginTransaction()
                        .add(mFragmentBucketOptionRepeat.getId(), bucketOptionReaptFragment, Tag.BUCKET_OPTION_FRAGMENT_REPEAT)
                        .commit();
                mBtnBucketOptionRepeat.setVisibility(View.GONE);
            }

        }
    }

    private void addSubBucketList(List<Bucket> bucketList) {
        mainContentsFragment = (MainContentsFragment) getSupportFragmentManager().findFragmentByTag(Tag.BUCKET_VIEW_FRAGMENT_SUB_BUCKET_LIST);
        if (mainContentsFragment == null) {
            if (bucketList != null && bucketList.size() > 0) {
                mainContentsFragment = new MainContentsFragment(bucketList);
                getSupportFragmentManager().beginTransaction()
                        .add(mLayoutSubbucketList.getId(), mainContentsFragment, Tag.BUCKET_VIEW_FRAGMENT_SUB_BUCKET_LIST)
                        .commit();
            }

        } else {
            mainContentsFragment.refreshList(bucketList);
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
            bucket.setDescription(s.toString());
        }
    };

    @Override
    public void onOptionFragmentRemoved(String tag) {
        CustomBaseFragment bucketOptionFragment = (CustomBaseFragment) getSupportFragmentManager().findFragmentByTag(tag);
        if (bucketOptionFragment != null) {
            getSupportFragmentManager().beginTransaction().remove(bucketOptionFragment).commit();
        }
        if (Tag.BUCKET_OPTION_FRAGMENT_REPEAT.equals(tag)) {
            mBtnBucketOptionRepeat.setVisibility(View.VISIBLE);
        }
    }

    private void goAddBucket(Integer parentId) {
        Intent intent;
        intent = new Intent();
        intent.setClass(this, BucketAddActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.putExtra("parentId", parentId);
        intent.putExtra("parentBucket", bucket);
        startActivity(intent);
    }
}
