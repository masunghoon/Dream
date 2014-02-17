package com.vivavu.dream.facebook.callback;

import com.facebook.Session;
import com.facebook.SessionState;
import com.vivavu.dream.common.BaseActionBarActivity;

/**
 * Created by yuja on 14. 2. 17.
 */
public class CustomFacebookStatusCallback implements Session.StatusCallback{
    private BaseActionBarActivity context;

    public CustomFacebookStatusCallback(BaseActionBarActivity context) {
        this.context = context;
    }

    @Override
    public void call(Session session, SessionState state, Exception exception) {
        if (session.isOpened()) {
            context.getContext().setToken(session.getAccessToken(), "facebook");
            context.checkAppExit();
        }
    }
}
