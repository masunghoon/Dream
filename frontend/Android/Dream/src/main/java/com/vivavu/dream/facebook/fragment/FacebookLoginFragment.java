package com.vivavu.dream.facebook.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.LoginButton;
import com.vivavu.dream.R;
import com.vivavu.dream.activity.intro.IntroActivity;
import com.vivavu.dream.common.BaseActionBarActivity;
import com.vivavu.dream.fragment.CustomBaseFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yuja on 14. 2. 18.
 */
public class FacebookLoginFragment extends CustomBaseFragment {
    private static final String TAG = "FacebookLoginFragment";
    @InjectView(R.id.authButton)
    LoginButton mAuthButton;
    @InjectView(R.id.txt_facebook_login_explain)
    TextView mTxtFacebookLoginExplain;
    private UiLifecycleHelper uiHelper;

    private Session.StatusCallback callback = new Session.StatusCallback() {
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.include_facebook_login, container, false);
        ButterKnife.inject(this, rootView);
        List<String> readPermissions = new ArrayList<String>();
        readPermissions.add("basic_info");
        readPermissions.add("email");
        readPermissions.add("user_birthday");

        mAuthButton.setReadPermissions(readPermissions);
        mAuthButton.setFragment(this);

        if(getActivity() instanceof IntroActivity){
            mTxtFacebookLoginExplain.setVisibility(View.GONE);
        }

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiHelper = new UiLifecycleHelper(getActivity(), callback);
        uiHelper.onCreate(savedInstanceState);
    }

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            context.setToken(session.getAccessToken(), "facebook");

            /* SessionStateChange에서 activity를 실행시키니 중복실행되는 문제가 있음
            if(getActivity() instanceof BaseActionBarActivity){
                BaseActionBarActivity activity = (BaseActionBarActivity) getActivity();
                activity.checkAppExit();
            }*/
        } else if (state.isClosed()) {

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Session session = Session.getActiveSession();
        if (session != null && (session.isOpened() || session.isClosed())) {
            onSessionStateChange(session, session.getState(), null);
        }
        uiHelper.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Session.DEFAULT_AUTHORIZE_ACTIVITY_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                context.setToken(Session.getActiveSession().getAccessToken(), "facebook");
                if (getActivity() instanceof BaseActionBarActivity) {
                    BaseActionBarActivity activity = (BaseActionBarActivity) getActivity();
                    //activity.checkAppExit();
                    activity.setResult(Activity.RESULT_OK);
                    activity.finish();
                }
            }
            return;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }
}
