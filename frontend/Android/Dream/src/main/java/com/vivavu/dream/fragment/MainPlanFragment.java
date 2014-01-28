package com.vivavu.dream.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.vivavu.dream.R;
import com.vivavu.dream.adapter.bucket.PlanAdapter;
import com.vivavu.dream.common.CustomBaseFragment;
import com.vivavu.dream.model.bucket.Plan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yuja on 14. 1. 23.
 */
public class MainPlanFragment extends CustomBaseFragment {
    @InjectView(R.id.bucketList)
    ListView mBucketList;
    List<Plan> plans;
    private PlanAdapter planAdapter;

    public MainPlanFragment() {
        this(new ArrayList<Plan>());
    }

    public MainPlanFragment(List<Plan> plans) {
        this.plans = plans;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.inject(this, rootView);
        planAdapter = new PlanAdapter(getActivity(), R.layout.bucket_list_item, plans);
        mBucketList.setAdapter(planAdapter);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshList(this.plans);
    }

    public void refreshList(List<Plan> plans){
        if(context != null){
            planAdapter.setPlans(plans);
            Collections.reverse(this.plans);
            planAdapter.notifyDataSetChanged();
        }
    }

    public List<Plan> getPlans() {
        return planAdapter.getPlans();
    }

    public void setPlans(List<Plan> plans) {
        this.plans = plans;
    }
}
