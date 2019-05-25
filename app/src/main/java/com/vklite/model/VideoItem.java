package com.vklite.model;

import org.json.JSONException;
import org.json.JSONObject;

public class VideoItem {

    String videoTitle = null;
    String videoPreviewImageURL = null;
    String videoURl = null;
    String videoViews = null;

    public VideoItem(JSONObject video) {
        try {
            this.videoTitle = video.getString("title");

            this.videoPreviewImageURL = video.getString("photo_320");

            String[] parts = video.getString("player").split("__ref");
            String str = parts[0];
            this.videoURl = str.substring(0, str.length() - 1);

            this.videoViews = video.getString("views") + " views";
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getVideoTitle() {
        return videoTitle;
    }

    public String getVideoPreviewImageURL() {
        return videoPreviewImageURL;
    }

    public String getVideoURl() {
        return videoURl;
    }

    public String getVideoViews() {
        return videoViews;
    }
}
