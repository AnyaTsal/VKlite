package com.vklite.model;

import org.json.JSONException;
import org.json.JSONObject;

public class GroupItem {
    private String groupName;
    private String imageURL;

    public GroupItem(JSONObject group) {
        try {
            this.groupName = group.getString("name");
            this.imageURL = group.getString("photo_100");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return groupName;
    }

    public String getImageURL() {
        return imageURL;
    }
}