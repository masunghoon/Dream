package com.vivavu.dream.util;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * Created by yuja on 14. 2. 6.
 */
public class AndroidUtils {
    public static void hideSoftInputFromWindow(Context context, View view){
        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        return;
    }

    /**
     * show virtual keyboard
     * @param context activity or application context
     * @param view EditText View or other View
     */
    public static void showSoftInputFromWindow(Context context, View view){
        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
        return;
    }

    /**
     * if parameter view is instanceof EditText view, show softkeyboard
     * else hide softkeyboard
     * @param context
     * @param view
     */
    public static void autoVisibleSoftInputFromWindow(Context context, View view){
        if( !(view instanceof EditText) ){
            AndroidUtils.hideSoftInputFromWindow(context, view);
        }else{
            AndroidUtils.showSoftInputFromWindow(context, view);
        }
    }

    public static void autoVisibleSoftInputFromWindow(View view){
        if( !(view instanceof EditText) ){
            AndroidUtils.hideSoftInputFromWindow(view.getContext(), view);
        }else{
            AndroidUtils.showSoftInputFromWindow(view.getContext(), view);
        }
    }

    public static View getRootView(Activity activity){
        View view1 = activity.getWindow().getDecorView().findViewById(android.R.id.content);
        View view2 = activity.findViewById(android.R.id.content);
        View view3 = activity.findViewById(android.R.id.content).getRootView();

        return view2;
    }

}
