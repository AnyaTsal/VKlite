package com.vklite.model;

import org.json.JSONException;
import org.json.JSONObject;

public class PhotoItem {

    public int photoLikesCount;
    public int isFavorite;
    private String photoRepostsCount, photoId, photoURL;

    public int isFavorite() {
        return isFavorite;
    }

    public String getPhotoId() {
        return photoId;
    }

    public int getPhotoLikesCount() {
        return photoLikesCount;
    }

    public String getPhotoRepostsCount() {
        return photoRepostsCount;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public PhotoItem(JSONObject item) {
        try {
            if (item.has("photo_807")) {
                this.photoURL = item.getString("photo_807");
            }
            else {
                this.photoURL = item.getString("photo_604");
            }
            if (item.has("likes")) {
                this.photoLikesCount = item.getJSONObject("likes").getInt("count");
            }
            if (item.has("reposts")) {
                this.photoRepostsCount = item.getJSONObject("reposts").getString("count");
            }
            this.photoId = item.getString("id");
            if (item.has("likes")) {
                this.isFavorite = item.getJSONObject("likes").getInt("user_likes");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
