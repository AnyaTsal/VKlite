package com.vklite.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class User implements Serializable {
    private String imageURL, userName;
    private long userId;

    public User(String imageURL, String userName, long userID) {
        this.imageURL = imageURL;
        this.userName = userName;
        this.userId = userID;
    }

    public User(JSONObject response) {
        try {
            imageURL = response.getString("photo_100");
            userName = response.getString("first_name") + " " + response.getString("last_name");
            userId = response.getLong("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return userName;
    }

    public String getImageURL() {
        return imageURL;
    }

    public long getID() {
        return userId;
    }
}