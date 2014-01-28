package com.vivavu.dream.adapter.bucket;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.vivavu.dream.R;
import com.vivavu.dream.model.bucket.Bucket;
import com.vivavu.dream.model.bucket.Plan;
import com.vivavu.dream.repository.DataRepository;
import com.vivavu.dream.util.DateUtils;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yuja on 14. 1. 9.
 */
public class PlanAdapter extends BaseAdapter implements View.OnClickListener {


    private LayoutInflater mInflater;
    private List<Plan> plans;
    private Context _context;
    private int resource;
    private PlanAdapterViewHolder planAdapterViewHolder;

    public PlanAdapter(Context context, int resource, List<Plan> plans) {
        super();
        this._context = context;
        this.resource = resource;
        this.mInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.plans = plans;
    }

    @Override
    public int getCount() {
        return plans.size();
    }

    @Override
    public Bucket getItem(int i) {
        return plans.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        int res = R.layout.plan_list_item;
        Plan item = plans.get(i);
        if (view == null) {
            view = mInflater.inflate(res, viewGroup, false);

            planAdapterViewHolder = new PlanAdapterViewHolder(view);
        }

        planAdapterViewHolder.mPlanItemDate.setText(DateUtils.getDateString(item.getDate(), "yyyy-MM-dd"));

        planAdapterViewHolder.mBucketItemTitle.setText(item.getTitle());
        planAdapterViewHolder.mBucketItemTitle.setOnClickListener(this);
        planAdapterViewHolder.mBucketItemTitle.setTag(item.getId());

        planAdapterViewHolder.mBucketBtnDone.setOnClickListener(this);
        planAdapterViewHolder.mBucketBtnDone.setEnabled(true);
        planAdapterViewHolder.mBucketBtnDone.setSelected(item.getIsDone());
        planAdapterViewHolder.mBucketBtnDone.setTag(item.getId());

        planAdapterViewHolder.mBucketItemScope.setText(item.getRange());
        planAdapterViewHolder.mBucketItemRemain.setText("remain " + DateUtils.getRemainDay(item.getDeadline()).toString() + " Days");

        if (item.getTodos() == null || item.getTodos().size() < 1) {
            planAdapterViewHolder.mBucketItemProgressbar.setVisibility(ProgressBar.GONE);
        } else {
            planAdapterViewHolder.mBucketItemProgressbar.setVisibility(ProgressBar.VISIBLE);
            planAdapterViewHolder.mBucketItemProgressbar.setProgress(item.getProgress());
        }
        planAdapterViewHolder.mBucketItemProgressbar.setEnabled(true);

        view.setTag(item);
        return view;
    }

    @Override
    public void onClick(final View view) {
        switch (view.getId()) {
            case R.id.bucket_item_title:
            case R.id.bucket_btn_done:
                //R.layout.dialog는 xml 파일명이고  R.id.popup은 보여줄 레이아웃 아이디
                View layout = mInflater.inflate(R.layout.popup_plan_done, null);
                final PlanPopupDoneViewHolder planPopupDoneViewHolder = new PlanPopupDoneViewHolder(layout);

                AlertDialog.Builder aDialog = new AlertDialog.Builder(_context);

                aDialog.setTitle("완료확인"); //타이틀바 제목
                aDialog.setView(layout); //dialog.xml 파일을 뷰로 셋팅

                aDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Plan plan = new Plan();
                        plan.setId((Integer) view.getTag());
                        int checkedButtonId = planPopupDoneViewHolder.mRadioGroupPopupPlanDone.getCheckedRadioButtonId();
                        plan.setIsDone(checkedButtonId == planPopupDoneViewHolder.mRadioBtnDoneTrue.getId());

                        DataRepository.updatePlanStatus(plan);
                    }
                });
                //그냥 닫기버튼을 위한 부분
                aDialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                //팝업창 생성
                AlertDialog ad = aDialog.create();
                ad.show();//보여줌!
        }
    }

    private Context getContext() {
        return this._context;
    }

    public void notifyDataSetChanged(List<Plan> plans) {
        this.plans.clear();
        this.plans.addAll(plans);

        super.notifyDataSetChanged();
    }

    public List<Plan> getPlans() {
        return plans;
    }

    public void setPlans(List<Plan> plans) {
        this.plans = plans;
    }


    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'null'
     * for easy to all layout elements.
     *
     * @author Android Butter Zelezny, plugin for IntelliJ IDEA/Android Studio by Inmite (www.inmite.eu)
     */
    static class PlanAdapterViewHolder {
        @InjectView(R.id.plan_item_date)
        TextView mPlanItemDate;
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
        @InjectView(R.id.bucket_item_btn_like)
        Button mBucketItemBtnLike;
        @InjectView(R.id.bucket_item_btn_reply)
        Button mBucketItemBtnReply;

        PlanAdapterViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'null'
     * for easy to all layout elements.
     *
     * @author Android Butter Zelezny, plugin for IntelliJ IDEA/Android Studio by Inmite (www.inmite.eu)
     */
    static class PlanPopupDoneViewHolder {
        @InjectView(R.id.radio_btn_done_true)
        RadioButton mRadioBtnDoneTrue;
        @InjectView(R.id.radio_btn_done_false)
        RadioButton mRadioBtnDoneFalse;
        @InjectView(R.id.radio_group_popup_plan_done)
        RadioGroup mRadioGroupPopupPlanDone;
        @InjectView(R.id.btn_popup_plan_camera)
        Button mBtnPopupPlanCamera;
        @InjectView(R.id.btn_popup_plan_share_facebook)
        Button mBtnPopupPlanShareFacebook;
        @InjectView(R.id.btn_popup_plan_share_twitter)
        Button mBtnPopupPlanShareTwitter;
        @InjectView(R.id.btn_popup_plan_share_google_plus)
        Button mBtnPopupPlanShareGooglePlus;

        PlanPopupDoneViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
