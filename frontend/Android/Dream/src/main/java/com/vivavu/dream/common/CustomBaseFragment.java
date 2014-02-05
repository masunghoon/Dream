package com.vivavu.dream.common;

import android.app.Activity;
import android.support.v4.app.Fragment;

/**
 * Created by yuja on 14. 1. 24.
 */
public abstract class CustomBaseFragment extends Fragment {
    protected DreamApp context;
    protected OnOptionFragmentRemovedListener mListener;

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
