package com.vivavu.dream.common;

import android.app.Application;

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
        DataRepository.setContext(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
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
