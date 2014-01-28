package com.vivavu.dream.fragment.bucket;

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
import com.vivavu.dream.common.CustomBaseFragment;
import com.vivavu.dream.common.RepeatType;
import com.vivavu.dream.common.Tag;
import com.vivavu.dream.model.bucket.option.OptionRepeat;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yuja on 14. 1. 24.
 */
public class BucketOptionReaptFragment extends CustomBaseFragment implements View.OnClickListener, BucketOption<OptionRepeat> {
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

    private OptionRepeat optionRepeat;

    public BucketOptionReaptFragment(){
        this.optionRepeat = new OptionRepeat();
    }

    public BucketOptionReaptFragment(OptionRepeat optionRepeat){
        this.optionRepeat = optionRepeat;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(optionRepeat == null){
            optionRepeat = new OptionRepeat();
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
                if(edit.length() > 0){
                    optionRepeat.setRepeatCount(Integer.parseInt(edit));
                }else{
                    optionRepeat.setRepeatCount(0);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        ArrayAdapter<RepeatType> repeatTypeArrayAdapter = new ArrayAdapter<RepeatType>(getActivity(), android.R.layout.simple_spinner_item, RepeatType.values());
        mSpinRepeatPeriod.setAdapter(repeatTypeArrayAdapter);
        mSpinRepeatPeriod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                RepeatType repeatType = (RepeatType) adapterView.getItemAtPosition(i);
                BucketOptionReaptFragment.this.optionRepeat.setRepeatType(repeatType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                BucketOptionReaptFragment.this.optionRepeat.setRepeatType(RepeatType.WKRP);
            }
        });

        bindData();

        return rootView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_bucket_option_repeat_custom:
                if (mLayoutBucketOptionRepeatCustom.getVisibility() == View.VISIBLE) {
                    mLayoutBucketOptionRepeatCustom.setVisibility(View.GONE);
                    optionRepeat.setRepeatType(RepeatType.WEEK);
                } else {
                    mLayoutBucketOptionRepeatCustom.setVisibility(View.VISIBLE);
                    optionRepeat.setRepeatType(RepeatType.MNTH);
                }

                break;
            case R.id.btn_bucket_option_sun:
                mBtnBucketOptionSun.setSelected(!mBtnBucketOptionSun.isSelected());
                optionRepeat.setSun(mBtnBucketOptionSun.isSelected());
                break;
            case R.id.btn_bucket_option_mon:
                mBtnBucketOptionMon.setSelected(!mBtnBucketOptionMon.isSelected());
                optionRepeat.setMon(mBtnBucketOptionMon.isSelected());
                break;
            case R.id.btn_bucket_option_tue:
                mBtnBucketOptionTue.setSelected(!mBtnBucketOptionTue.isSelected());
                optionRepeat.setTue(mBtnBucketOptionTue.isSelected());
                break;
            case R.id.btn_bucket_option_wen:
                mBtnBucketOptionWen.setSelected(!mBtnBucketOptionWen.isSelected());
                optionRepeat.setWen(mBtnBucketOptionWen.isSelected());
                break;
            case R.id.btn_bucket_option_thu:
                mBtnBucketOptionThu.setSelected(!mBtnBucketOptionThu.isSelected());
                optionRepeat.setThu(mBtnBucketOptionThu.isSelected());
                break;
            case R.id.btn_bucket_option_fri:
                mBtnBucketOptionFri.setSelected(!mBtnBucketOptionFri.isSelected());
                optionRepeat.setFri(mBtnBucketOptionFri.isSelected());
                break;
            case R.id.btn_bucket_option_sat:
                mBtnBucketOptionSat.setSelected(!mBtnBucketOptionSat.isSelected());
                optionRepeat.setSat(mBtnBucketOptionSat.isSelected());
                break;
            case R.id.btn_bucket_option_repeat_cnt:
                optionRepeat.setRepeatType(RepeatType.NONE);
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

    public void bindData(){
        if(optionRepeat.getRepeatType() == RepeatType.WKRP){
            mBtnBucketOptionSun.setSelected(optionRepeat.isSun());
            mBtnBucketOptionMon.setSelected(optionRepeat.isMon());
            mBtnBucketOptionTue.setSelected(optionRepeat.isTue());
            mBtnBucketOptionWen.setSelected(optionRepeat.isWen());
            mBtnBucketOptionThu.setSelected(optionRepeat.isThu());
            mBtnBucketOptionFri.setSelected(optionRepeat.isFri());
            mBtnBucketOptionSat.setSelected(optionRepeat.isSat());

        } else if(optionRepeat.getRepeatType() == RepeatType.WEEK){
            mSpinRepeatPeriod.setSelection(RepeatType.WEEK.ordinal());
            mTxtBucketOptionRepeatCnt.setText(String.valueOf(optionRepeat.getRepeatCount()));
            mLayoutBucketOptionRepeatCustom.setVisibility(View.VISIBLE);
        } else if(optionRepeat.getRepeatType() == RepeatType.MNTH){
            mSpinRepeatPeriod.setSelection(RepeatType.MNTH.ordinal());
            mTxtBucketOptionRepeatCnt.setText(String.valueOf(optionRepeat.getRepeatCount()));
            mLayoutBucketOptionRepeatCustom.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public OptionRepeat getContents() {
        return optionRepeat;
    }

}
