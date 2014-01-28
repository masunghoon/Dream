package com.vivavu.dream.model;

import com.vivavu.dream.model.user.User;

/**
 * Created by yuja on 14. 1. 13.
 */
public class SecureToken {
    private String token;
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "SecureToken{" +
                "token='" + token + '\'' +
                ", user=" + user +
                '}';
    }
}
