package com.vivavu.dream.fragment.bucket;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.vivavu.dream.R;
import com.vivavu.dream.activity.bucket.BucketAddActivity;
import com.vivavu.dream.common.Code;
import com.vivavu.dream.fragment.CustomBaseFragment;
import com.vivavu.dream.common.RepeatType;
import com.vivavu.dream.common.Tag;
import com.vivavu.dream.model.bucket.Bucket;
import com.vivavu.dream.model.bucket.option.OptionRepeat;
import com.vivavu.dream.repository.DataRepository;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yuja on 14. 1. 25.
 */
public class BucketCardFragment extends CustomBaseFragment implements View.OnClickListener{

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
    @InjectView(R.id.fragment_bucket_option_repeat)
    LinearLayout mFragmentBucketOptionRepeat;
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

    private Bucket bucket;

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

    public BucketCardFragment(){
        bucket = new Bucket();
    }

    public BucketCardFragment(Bucket bucket) {
        this.bucket = bucket;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.bucket_item_view, container, false);
        ButterKnife.inject(this, rootView);

        bindEventListener();
        bindData();

        return rootView;
    }

    private void bindEventListener() {
        mBucketBtnDone.setOnClickListener(this);
        mBucketItemTitle.setOnClickListener(this);
        mBtnBucketOptionNote.setOnClickListener(this);
        mBucketOptionNote.addTextChangedListener(textWatcherInput);
        mBtnBucketOptionPublic.setOnClickListener(this);
        mBtnOptionRemove.setOnClickListener(this);
        mBtnBucketOptionDel.setOnClickListener(this);
        mBtnBucketOptionRepeat.setOnClickListener(this);
    }

    private void bindData(){
        //mBucketBtnDone.setSelected(bucket.getIsLive() == 1);

        mBucketItemTitle.setText(bucket.getTitle());
        mBucketItemTitle.setTag(bucket.getId());

        mBucketItemScope.setText(bucket.getRange());

        mBucketItemRemain.setText(bucket.getRemainDays());

        /*if (bucket.getTodos() == null || bucket.getTodos().size() < 1) {
            mBucketItemProgressbar.setVisibility(ProgressBar.GONE);
        } else {
            mBucketItemProgressbar.setVisibility(ProgressBar.VISIBLE);
            mBucketItemProgressbar.setProgress(bucket.getProgress());
        }*/

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
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bucket_item_title:
                Intent intent = new Intent(getActivity(), BucketAddActivity.class);
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
                    Toast.makeText(getActivity(), "비공개", Toast.LENGTH_SHORT).show();
                } else {
                    bucket.setIsPrivate(0);
                    Toast.makeText(getActivity(), "공개", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.bucket_btn_done:
                view.setSelected(!view.isSelected());
                if (view.isSelected()) {
                    //bucket.setIsLive(1);
                    Toast.makeText(getActivity(), "완료", Toast.LENGTH_SHORT).show();
                } else {
                    //bucket.setIsLive(0);
                    Toast.makeText(getActivity(), "진행중", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_option_remove:
                bucket.setDescription(null);
                bindData();

                break;
            case R.id.btn_bucket_option_del:
                AlertDialog.Builder alert_confirm = new AlertDialog.Builder(getActivity());
                alert_confirm.setMessage("삭제하시겠습니까?").setCancelable(false).setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 'YES' 제거 로직 추가
                                DataRepository.deleteBucket(bucket.getId());
                                Toast.makeText(getActivity(), "삭제확인", Toast.LENGTH_SHORT).show();
                                getActivity().finish();
                            }
                        }).setNegativeButton("취소",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 'No'
                                Toast.makeText(getActivity(), "삭제취소", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        });
                AlertDialog alert = alert_confirm.create();
                alert.show();
                break;
            case R.id.btn_bucket_option_share:
                Toast.makeText(getActivity(), "공유하기", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_bucket_option_repeat:
                //Todo: 임시로 addOptionRepeat()을 위해 bucket에 값을 넣음, 초기에 option카드를 그릴지 말지 결정하는 다른 알고리즘 필요
                bucket.setRptType(RepeatType.WKRP.getCode());
                addOptionRepeat();
                break;
        }
    }

    private void addOptionRepeat(){
        //getChildFragmentManager()는 fragment안에 fragment를 넣기 위함
        BucketOptionRepeatFragment bucketOptionReaptFragment = (BucketOptionRepeatFragment) getChildFragmentManager().findFragmentByTag(Tag.BUCKET_OPTION_FRAGMENT_REPEAT);
        if(bucketOptionReaptFragment == null){
            if(bucket.getRptType() != null) {
                OptionRepeat optionRepeat = new OptionRepeat();
                optionRepeat.setRepeatType(RepeatType.fromCode(bucket.getRptType()));
                optionRepeat.setOptionStat(bucket.getRptCndt());
                bucketOptionReaptFragment = new BucketOptionRepeatFragment(optionRepeat);
                getChildFragmentManager().beginTransaction()
                        .add(mFragmentBucketOptionRepeat.getId(), bucketOptionReaptFragment, Tag.BUCKET_OPTION_FRAGMENT_REPEAT)
                        .commit();
                mBtnBucketOptionRepeat.setVisibility(View.GONE);
            }

        }
    }

}
