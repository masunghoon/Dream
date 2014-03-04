package com.vivavu.dream.fragment.main;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;

import com.vivavu.dream.R;

/**
 * Created by yuja on 14. 3. 4.
 */
public class NoticeDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater mLayoutInflater = getActivity().getLayoutInflater();
        mBuilder.setView(mLayoutInflater.inflate(R.layout.actionbar_notice, null));
        mBuilder.setTitle("Notice");
        mBuilder.setMessage("없다");
        return mBuilder.create();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
