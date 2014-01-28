package com.vivavu.dream.model.user;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

/**
 * Created by yuja on 14. 1. 8.
 */
public class Users {
    @JsonProperty("users")
    private List<User> users;

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    @Override
    public String toString() {
        return "Users{" +
                "users=" + users +
                '}';
    }
}
