package com.vklite.model;

import org.json.JSONException;
import org.json.JSONObject;

public class PostLink {

    private String linkURL, title;

    PostLink(JSONObject link) {
        try {
            this.linkURL = link.getString("url");
            this.title = link.getString("title");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getUrl() {
        return linkURL;
    }

    public String getTitle() {
        return title;
    }
}
