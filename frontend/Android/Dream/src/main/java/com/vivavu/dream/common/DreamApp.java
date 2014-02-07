package com.vivavu.dream.common;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;

import com.vivavu.dream.MainActivity;
import com.vivavu.dream.model.BaseInfo;
import com.vivavu.dream.model.user.User;
import com.vivavu.dream.repository.DataRepository;

/**
 * Created by yuja on 14. 1. 17.
 */
public class DreamApp extends Application {
    public static final String LOG_TAG = "dream";
    public static boolean debugMode = true;

    private User user  = null;
    private String token = null;
    private String username = null;
    private String email = null;
    private String password = null;

    private boolean login=false;

    @Override
    public void onCreate() {
        super.onCreate();
        loadAppDefaultInfo();
        DataRepository.setContext(this);
    }

    @Override
    public void onTerminate() {
        saveAppDefaultInfo();
        super.onTerminate();
    }

    public void logout(){
        setLogin(false);
        setUser(null);
        setToken(null);
        saveAppDefaultInfo();

        Intent intent = new Intent();
        intent.setClass(this, MainActivity.class);
        // activity 외부에서 activity 실행시 FLAG_ACTIVITY_NEW_TASK 를 넣어주어야한다.
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public boolean checkLogin() {
        if(isLogin() == false){
            BaseInfo baseInfo = DataRepository.getBaseInfo();
            if (baseInfo != null) {
                setUser(baseInfo);
                setUsername(baseInfo.getUsername());
                setLogin(true);
                return true;
            }
            setLogin(false);
            return false;
        }
        return true;
    }

    public void loadAppDefaultInfo() {

        //기존 저장된 로그인 관련 정보 불러오기
        SharedPreferences settings = getSharedPreferences(Constants.settings, MODE_PRIVATE);
        String email = settings.getString(Constants.email, "");
        String token = settings.getString(Constants.token, "");

        setEmail(email);
        setToken(token);
    }

    public void saveAppDefaultInfo() {

        // 프리퍼런스 설정
        SharedPreferences prefs = getSharedPreferences(Constants.settings, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(Constants.email, getEmail());
        editor.putString(Constants.token, getToken());   // String

        editor.commit();
    }

    public static String getLogTag() {
        return LOG_TAG;
    }

    public static boolean isDebugMode() {
        return debugMode;
    }

    public static void setDebugMode(boolean debugMode) {
        DreamApp.debugMode = debugMode;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isLogin() {
        return login;
    }

    public void setLogin(boolean login) {
        this.login = login;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        if(username == null){
            return getUser().getUsername();
        }
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
