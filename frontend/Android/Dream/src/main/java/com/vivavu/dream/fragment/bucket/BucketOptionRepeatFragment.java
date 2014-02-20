package com.vivavu.dream.fragment.bucket;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.vivavu.dream.R;
import com.vivavu.dream.fragment.CustomBaseFragment;
import com.vivavu.dream.common.RepeatType;
import com.vivavu.dream.common.Tag;
import com.vivavu.dream.model.bucket.option.OptionRepeat;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yuja on 14. 1. 24.
 */
public class BucketOptionRepeatFragment extends CustomBaseFragment implements View.OnClickListener, BucketOption<OptionRepeat> {
    @InjectView(R.id.btn_bucket_option_sun)
    Button mBtnBucketOptionSun;
    @InjectView(R.id.btn_bucket_option_mon)
    Button mBtnBucketOptionMon;
    @InjectView(R.id.btn_bucket_option_tue)
    Button mBtnBucketOptionTue;
    @InjectView(R.id.btn_bucket_option_wen)
    Button mBtnBucketOptionWen;
    @InjectView(R.id.btn_bucket_option_thu)
    Button mBtnBucketOptionThu;
    @InjectView(R.id.btn_bucket_option_fri)
    Button mBtnBucketOptionFri;
    @InjectView(R.id.btn_bucket_option_sat)
    Button mBtnBucketOptionSat;
    @InjectView(R.id.btn_bucket_option_repeat_custom)
    Button mBtnBucketOptionRepeatCustom;
    @InjectView(R.id.txt_bucket_option_repeat_cnt)
    EditText mTxtBucketOptionRepeatCnt;
    @InjectView(R.id.btn_bucket_option_repeat_cnt)
    Button mBtnBucketOptionRepeatCnt;
    @InjectView(R.id.spin_repeat_period)
    Spinner mSpinRepeatPeriod;
    @InjectView(R.id.layout_bucket_option_repeat_custom)
    LinearLayout mLayoutBucketOptionRepeatCustom;
    @InjectView(R.id.btn_bucket_option_repeat_save)
    Button mBtnBucketOptionRepeatSave;
    @InjectView(R.id.btn_bucket_option_repeat_cancel)
    Button mBtnBucketOptionRepeatCancel;
    @InjectView(R.id.layout_bucket_option_repeat_week)
    LinearLayout mLayoutBucketOptionRepeatWeek;

    private OptionRepeat userInputOptionRepeat;
    private OptionRepeat originalOptionRepeat;
    private OnOptionFragmentRemovedListener mListener;

    public BucketOptionRepeatFragment() {
        this.userInputOptionRepeat = new OptionRepeat();
        this.originalOptionRepeat = new OptionRepeat();
    }

    public BucketOptionRepeatFragment(OptionRepeat optionRepeat) {
        this.originalOptionRepeat = optionRepeat;
        this.userInputOptionRepeat = (OptionRepeat) optionRepeat.clone();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (userInputOptionRepeat == null) {
            userInputOptionRepeat = new OptionRepeat();
            originalOptionRepeat = new OptionRepeat();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.bucket_option_repeat, container, false);
        ButterKnife.inject(this, rootView);
        mLayoutBucketOptionRepeatCustom.setVisibility(View.GONE);

        mBtnBucketOptionRepeatCustom.setOnClickListener(this);
        mBtnBucketOptionSun.setOnClickListener(this);
        mBtnBucketOptionMon.setOnClickListener(this);
        mBtnBucketOptionTue.setOnClickListener(this);
        mBtnBucketOptionWen.setOnClickListener(this);
        mBtnBucketOptionThu.setOnClickListener(this);
        mBtnBucketOptionFri.setOnClickListener(this);
        mBtnBucketOptionSat.setOnClickListener(this);

        mBtnBucketOptionRepeatSave.setOnClickListener(this);
        mBtnBucketOptionRepeatCancel.setOnClickListener(this);

        mBtnBucketOptionRepeatCnt.setOnClickListener(this);
        mTxtBucketOptionRepeatCnt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                String edit = charSequence.toString();
                if (edit.length() > 0) {
                    userInputOptionRepeat.setRepeatCount(Integer.parseInt(edit));
                } else {
                    userInputOptionRepeat.setRepeatCount(0);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        List<RepeatType> typeArray = new ArrayList<RepeatType>();
        typeArray.add(RepeatType.WEEK);
        typeArray.add(RepeatType.MNTH);
        ArrayAdapter<RepeatType> repeatTypeArrayAdapter = new ArrayAdapter<RepeatType>(getActivity(), android.R.layout.simple_spinner_item, typeArray);
        mSpinRepeatPeriod.setAdapter(repeatTypeArrayAdapter);
        mSpinRepeatPeriod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                RepeatType repeatType = (RepeatType) adapterView.getItemAtPosition(i);
                BucketOptionRepeatFragment.this.userInputOptionRepeat.setRepeatType(repeatType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //BucketOptionRepeatFragment.this.userInputOptionRepeat.setRepeatType(RepeatType.WKRP);
            }
        });

        bindData();

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try{
            mListener = (OnOptionFragmentRemovedListener) activity;
        }catch (ClassCastException e){
            throw new ClassCastException(activity.toString() + " must implement OnOptionFragmentRemovedListener");
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);//배경선택시 키보드 없애기 위해 호출
        switch (view.getId()) {
            case R.id.btn_bucket_option_repeat_custom:
                if (mLayoutBucketOptionRepeatCustom.getVisibility() == View.VISIBLE) {
                    mLayoutBucketOptionRepeatCustom.setVisibility(View.GONE);
                    mLayoutBucketOptionRepeatWeek.setVisibility(View.VISIBLE);
                } else {
                    mLayoutBucketOptionRepeatCustom.setVisibility(View.VISIBLE);
                    mLayoutBucketOptionRepeatWeek.setVisibility(View.GONE);
                }

                break;
            case R.id.btn_bucket_option_sun:
                mBtnBucketOptionSun.setSelected(!mBtnBucketOptionSun.isSelected());
                userInputOptionRepeat.setSun(mBtnBucketOptionSun.isSelected());
                break;
            case R.id.btn_bucket_option_mon:
                mBtnBucketOptionMon.setSelected(!mBtnBucketOptionMon.isSelected());
                userInputOptionRepeat.setMon(mBtnBucketOptionMon.isSelected());
                break;
            case R.id.btn_bucket_option_tue:
                mBtnBucketOptionTue.setSelected(!mBtnBucketOptionTue.isSelected());
                userInputOptionRepeat.setTue(mBtnBucketOptionTue.isSelected());
                break;
            case R.id.btn_bucket_option_wen:
                mBtnBucketOptionWen.setSelected(!mBtnBucketOptionWen.isSelected());
                userInputOptionRepeat.setWen(mBtnBucketOptionWen.isSelected());
                break;
            case R.id.btn_bucket_option_thu:
                mBtnBucketOptionThu.setSelected(!mBtnBucketOptionThu.isSelected());
                userInputOptionRepeat.setThu(mBtnBucketOptionThu.isSelected());
                break;
            case R.id.btn_bucket_option_fri:
                mBtnBucketOptionFri.setSelected(!mBtnBucketOptionFri.isSelected());
                userInputOptionRepeat.setFri(mBtnBucketOptionFri.isSelected());
                break;
            case R.id.btn_bucket_option_sat:
                mBtnBucketOptionSat.setSelected(!mBtnBucketOptionSat.isSelected());
                userInputOptionRepeat.setSat(mBtnBucketOptionSat.isSelected());
                break;
            case R.id.btn_bucket_option_repeat_cnt:
                userInputOptionRepeat.setRepeatType(RepeatType.NONE);
                AlertDialog.Builder alert_confirm = new AlertDialog.Builder(getActivity());
                alert_confirm.setMessage("삭제하시겠습니까?").setCancelable(false).setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 'YES' 제거 로직 추가
                                mListener.onOptionFragmentRemoved(Tag.BUCKET_OPTION_FRAGMENT_REPEAT);
                            }
                        }).setNegativeButton("취소",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 'No'
                                return;
                            }
                        });
                AlertDialog alert = alert_confirm.create();
                alert.show();
                break;
            case R.id.btn_bucket_option_repeat_save:
                break;
            case R.id.btn_bucket_option_repeat_cancel:
                break;

        }
    }

    public void bindData() {
        if (userInputOptionRepeat.getRepeatType() == RepeatType.WKRP) {
            mBtnBucketOptionSun.setSelected(userInputOptionRepeat.isSun());
            mBtnBucketOptionMon.setSelected(userInputOptionRepeat.isMon());
            mBtnBucketOptionTue.setSelected(userInputOptionRepeat.isTue());
            mBtnBucketOptionWen.setSelected(userInputOptionRepeat.isWen());
            mBtnBucketOptionThu.setSelected(userInputOptionRepeat.isThu());
            mBtnBucketOptionFri.setSelected(userInputOptionRepeat.isFri());
            mBtnBucketOptionSat.setSelected(userInputOptionRepeat.isSat());
            mLayoutBucketOptionRepeatCustom.setVisibility(View.GONE);
            mLayoutBucketOptionRepeatWeek.setVisibility(View.VISIBLE);
        } else if (userInputOptionRepeat.getRepeatType() == RepeatType.WEEK) {
            mSpinRepeatPeriod.setSelection(0);
            mTxtBucketOptionRepeatCnt.setText(String.valueOf(userInputOptionRepeat.getRepeatCount()));
            mLayoutBucketOptionRepeatCustom.setVisibility(View.VISIBLE);
            mLayoutBucketOptionRepeatWeek.setVisibility(View.GONE);
        } else if (userInputOptionRepeat.getRepeatType() == RepeatType.MNTH) {
            mSpinRepeatPeriod.setSelection(1);
            mTxtBucketOptionRepeatCnt.setText(String.valueOf(userInputOptionRepeat.getRepeatCount()));
            mLayoutBucketOptionRepeatCustom.setVisibility(View.VISIBLE);
            mLayoutBucketOptionRepeatWeek.setVisibility(View.GONE);
        }
    }

    @Override
    public OptionRepeat getContents() {
        if(mLayoutBucketOptionRepeatWeek.getVisibility() == View.VISIBLE){
            userInputOptionRepeat.setRepeatType(RepeatType.WKRP);
        } else {
            RepeatType repeatType = (RepeatType) mSpinRepeatPeriod.getSelectedItem();
            userInputOptionRepeat.setRepeatType(repeatType);
        }

        return userInputOptionRepeat;
    }

    public interface OnOptionFragmentRemovedListener{
        public void onOptionFragmentRemoved(String tag);
    }
}
