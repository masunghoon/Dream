package com.vivavu.dream.fragment;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.View;

import com.vivavu.dream.common.DreamApp;
import com.vivavu.dream.util.AndroidUtils;

/**
 * Created by yuja on 14. 1. 24.
 */
public class CustomBaseFragment extends Fragment implements View.OnClickListener{
    protected DreamApp context;
    protected OnOptionFragmentRemovedListener mListener;

    @Override
    public void onClick(View view) {
        //todo:이게 호출되는지 확인 필요.
        AndroidUtils.autoVisibleSoftInputFromWindow(view);
    }

    public interface OnOptionFragmentRemovedListener{
        public void onOptionFragmentRemoved(String tag);
    }

    public CustomBaseFragment(){
        super();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = (DreamApp) getActivity().getApplicationContext();
        try{
            mListener = (OnOptionFragmentRemovedListener) activity;
        }catch (ClassCastException e){
            throw new ClassCastException(activity.toString() + " must implement OnArticleSelectedListener");
        }

    }

    public DreamApp getContext() {
        return context;
    }
}