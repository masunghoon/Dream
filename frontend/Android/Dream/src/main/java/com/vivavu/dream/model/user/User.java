package com.vivavu.dream.model.user;

import com.vivavu.dream.util.JsonDateDeserializer;
import com.vivavu.dream.util.JsonDateSerializer;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.Date;

/**
 * Created by yuja on 14. 1. 7.
 */
public class User{
    @JsonProperty("about_me")
    private String aboutMe;
    @JsonProperty("birthday")
    private String birthday;
    @JsonProperty("email")
    private String email;
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("is_following")
    private Boolean isFollowing;

    @JsonSerialize(using= JsonDateSerializer.class)
    @JsonDeserialize(using= JsonDateDeserializer.class)
    @JsonProperty("last_seen")
    private Date lastSeen;
    @JsonProperty("pic")
    private String pic;
    @JsonProperty("uri")
    private String uri;
    @JsonProperty("username")
    private String username;

    public String getAboutMe() {
        return aboutMe;
    }

    public void setAboutMe(String aboutMe) {
        this.aboutMe = aboutMe;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getIsFollowing() {
        return isFollowing;
    }

    public void setIsFollowing(Boolean isFollowing) {
        this.isFollowing = isFollowing;
    }

    public Date getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(Date lastSeen) {
        this.lastSeen = lastSeen;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "User{" +
                "aboutMe='" + aboutMe + '\'' +
                ", birthday='" + birthday + '\'' +
                ", email='" + email + '\'' +
                ", id=" + id +
                ", isFollowing=" + isFollowing +
                ", lastSeen=" + lastSeen +
                ", pic='" + pic + '\'' +
                ", uri='" + uri + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
