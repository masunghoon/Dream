package com.vivavu.dream.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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

    public static void getKeyHashes(Context context){
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    "com.vivavu.dream",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }


}
