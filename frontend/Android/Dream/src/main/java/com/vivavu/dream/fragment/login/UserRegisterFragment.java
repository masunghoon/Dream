package com.vivavu.dream.fragment.login;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;

import com.vivavu.dream.R;
import com.vivavu.dream.fragment.CustomProgressFragment;
import com.vivavu.dream.model.LoginInfo;
import com.vivavu.dream.model.SecureToken;
import com.vivavu.dream.repository.DataRepository;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yuja on 14. 2. 11.
 */
public class UserRegisterFragment extends CustomProgressFragment {

    @InjectView(R.id.email)
    EditText mEmail;
    @InjectView(R.id.password)
    EditText mPassword;
    @InjectView(R.id.password_dup)
    EditText mPasswordDup;
    @InjectView(R.id.regist_button)
    Button mRegistButton;
    @InjectView(R.id.register_form)
    ScrollView mRegisterForm;

    public static UserRegisterFragment newInstance(){
        return new UserRegisterFragment();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Setup content view
        setContentView(R.layout.activity_register);

        setContentShown(true);
        ButterKnife.inject(this, getContentView());
        mRegistButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if(view == mRegistButton){
            attemptRegist();
        }
    }

    private void attemptRegist() {
        // Reset errors.
        mEmail.setError(null);
        mPassword.setError(null);

        // Store values at the time of the login attempt.
        String mEmailValue = mEmail.getText().toString();
        String mPasswordValue = mPassword.getText().toString();
        String mPasswordDupValue = mPasswordDup.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password.
        if (TextUtils.isEmpty(mPasswordValue)) {
            mPassword.setError(getString(R.string.error_field_required));
            focusView = mPassword;
            cancel = true;
        } else if (mPasswordValue.length() < 4) {
            mPassword.setError(getString(R.string.error_invalid_password));
            focusView = mPassword;
            cancel = true;
        } else if (!mPasswordValue.equals(mPasswordDupValue)){
            mPasswordDup.setError(getString(R.string.error_invalid_password_dup));
            focusView = mPasswordDup;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(mEmailValue)) {
            mEmail.setError(getString(R.string.error_field_required));
            focusView = mEmail;
            cancel = true;
        } else if (!mEmailValue.contains("@")) {
            mEmail.setError(getString(R.string.error_invalid_email));
            focusView = mEmail;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            //mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
            UserRegisterTask mAuthTask = new UserRegisterTask();

            LoginInfo user = new LoginInfo();
            user.setEmail(mEmailValue);
            user.setPassword(mPasswordValue);

            mAuthTask.execute(user);
        }
    }

    public class UserRegisterTask extends AsyncTask<LoginInfo, Void, Boolean> {

        @Override
        protected Boolean doInBackground(LoginInfo... params) {
            LoginInfo user = null;
            if (params.length > 0) {
                user = params[0];
            } else {
                return false;
            }

            SecureToken userInfo = DataRepository.registUser(user);
            if (userInfo == null) {
                return false;
            }

            context.setUser(userInfo.getUser());
            context.setUsername(userInfo.getUser().getUsername());
            context.setToken(userInfo.getToken());
            context.setTokenType("unused");
            context.saveAppDefaultInfo();

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            setContentShown(true);
            if (success) {
                context.setLogin(false);
                getActivity().setResult(Activity.RESULT_OK);
                getActivity().finish();
            } else {
                this.cancel(false);
                context.setLogin(false);
                mPassword.setError(getString(R.string.error_incorrect_password));
                mPassword.requestFocus();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            setContentShown(false);
        }

        @Override
        protected void onCancelled() {
            this.cancel(true);
            setContentShown(true);
            setContentEmpty(false);
        }
    }
}
