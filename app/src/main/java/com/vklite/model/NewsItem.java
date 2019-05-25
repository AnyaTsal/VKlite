package com.vklite.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class NewsItem {

    private String postData,
            postAuthorName, postAuthorImageURL,
            postText,
            postCommentsCount, postRepostsCount;
    public int sourceId, postId, postLikesCount, isFavorite;

    public int isFavorite() {
        return isFavorite;
    }

    private ArrayList<PhotoItem> imageList = null;
    private ArrayList<PostLink> linkList = null;

    public ArrayList<PhotoItem> getImageList() {
        return imageList;
    }

    public ArrayList<PostLink> getLinkList() {
        return linkList;
    }

    public int getPostLikesCount() {
        return postLikesCount;
    }

    public int getPostId() {
        return postId;
    }

    public int getSourceId() {
        return sourceId;
    }

    public String getPostCommentsCount() {
        return postCommentsCount;
    }

    public String getPostRepostsCount() {
        return postRepostsCount;
    }

    public NewsItem(JSONObject item, JSONArray groups, JSONArray profiles) {
        try {
            this.postData = Util.convertTime(item.getLong("date"));

            if (item.has("text") && !item.getString("text").isEmpty()) {
                this.postText = item.getString("text");
            }

            this.sourceId = item.getInt("source_id");
            if (sourceId < 0) {
                for (int i = 0; i < groups.length(); i++) {
                    if (groups.getJSONObject(i).getInt("id") == sourceId*(-1)) {
                        this.postAuthorName = groups.getJSONObject(i).getString("name");
                        this.postAuthorImageURL = groups.getJSONObject(i).getString("photo_100");
                        break;
                    }
                }
            } else {
                for (int i = 0; i < profiles.length(); i++) {
                    if (profiles.getJSONObject(i).getInt("id") == sourceId) {
                        this.postAuthorName = profiles.getJSONObject(i).getString("first_name") + " "
                                + profiles.getJSONObject(i).getString("last_name");
                        this.postAuthorImageURL = profiles.getJSONObject(i).getString("photo_100");
                        break;
                    }
                }
            }
            this.postLikesCount = item.getJSONObject("likes").getInt("count");
            this.postCommentsCount = item.getJSONObject("comments").getString("count");
            this.postRepostsCount = item.getJSONObject("reposts").getString("count");

            this.isFavorite = item.getJSONObject("likes").getInt("user_likes");

            this.postId = item.getInt("post_id");

            if (item.has("attachments")) {
                imageList = new ArrayList<>();
                linkList = new ArrayList<>();
                for (int i = 0; i < item.getJSONArray("attachments").length(); i++) {
                    if (item.getJSONArray("attachments").getJSONObject(i).getString("type").equals("photo")) {
                        PhotoItem image = new PhotoItem(item.getJSONArray("attachments").getJSONObject(i).getJSONObject("photo"));
                        imageList.add(image);
                    }
                    if (item.getJSONArray("attachments").getJSONObject(i).getString("type").equals("link")) {
                        PostLink link = new PostLink(item.getJSONArray("attachments").getJSONObject(i).getJSONObject("link"));
                        linkList.add(link);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getPostText() {
        return postText;
    }

    public String getPostAuthorName() {
        return postAuthorName;
    }

    public String getPostAuthorImageURL() {
        return postAuthorImageURL;
    }

    public String getPostData() {
        return postData;
    }
}
