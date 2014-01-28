package com.vivavu.dream.model.user;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Created by yuja on 14. 1. 8.
 */
public class UserWrapped {
    @JsonProperty("user")
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
