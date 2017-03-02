package com.example.hiennguyen.firebaseexample.model;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by hiennguyen on 20/02/2017
 */
@IgnoreExtraProperties
public class FoodDetail {
    private String addedByUser;
    private boolean completed;
    private String name;
    private String image;

    public FoodDetail() {
    }

    public FoodDetail(String addedByUser, boolean completed, String name, String image) {
        this.addedByUser = addedByUser;
        this.completed = completed;
        this.name = name;
        this.image = image;
    }

    public String getAddedByUser() {
        return addedByUser;
    }

    public void setAddedByUser(String addedByUser) {
        this.addedByUser = addedByUser;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
