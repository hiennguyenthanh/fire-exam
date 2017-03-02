package com.example.hiennguyen.firebaseexample.model;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by hiennguyen on 02/03/2017
 */

@IgnoreExtraProperties
public class User {
    private String userId;
    private String userName;
    private String profileUrl;

    public User() {
    }

    public User(String userName, String profileUrl) {
        this.userName = userName;
        this.profileUrl = profileUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }
}
