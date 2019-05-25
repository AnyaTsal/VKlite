package com.vklite.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.vklite.model.Util.convertTime;

public class DialogItem {
    private String dialogName;
    private String userMessage;
    private String messageTime;
    private String imageURL;
    private long userID;

    public DialogItem(String dialogName, String userMessage, String messageTime, String imageURL, long userID) {
        this.dialogName = dialogName;
        this.userMessage = userMessage;
        this.messageTime = messageTime;
        this.imageURL = imageURL;
        this.userID = userID;
    }

    public DialogItem(JSONObject message, JSONArray profiles) {
        parseMessage(message, profiles);
    }

    public String getDialogName() {
        return dialogName;
    }

    public String getUserMessage() {
        return userMessage;
    }

    public String getMessageTime() {
        return messageTime;
    }

    public long getUserID() {
        return userID;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }

    public void setMessageTime(String messageTime) {
        this.messageTime = messageTime;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public void setDialogName(String dialogName) {
        this.dialogName = dialogName;
    }

    private void parseMessage(JSONObject message, JSONArray profiles) {
        try {
            this.userMessage = message.getJSONObject("last_message").getString("text");
            this.messageTime = convertTime(message.getJSONObject("last_message").getLong("date"));

            this.userID = message.getJSONObject("conversation").getJSONObject("peer").getLong("id");
            String type = message.getJSONObject("conversation").getJSONObject("peer").getString("type");
            if (type.equals("user")) {
                for (int i = 0; i < profiles.length(); i++) {
                    if (profiles.getJSONObject(i).getLong("id") == userID) {
                        this.dialogName = profiles.getJSONObject(i).getString("first_name") + " "
                                + profiles.getJSONObject(i).getString("last_name");
                        this.imageURL = profiles.getJSONObject(i).getString("photo_100");
                        break;
                    }
                }
            }
            else {
                this.dialogName = message.getJSONObject("conversation").getJSONObject("chat_settings").getString("title");
                if (message.getJSONObject("conversation").getJSONObject("chat_settings").has("photo")) {
                    this.imageURL = message.getJSONObject("conversation").getJSONObject("chat_settings").getJSONObject("photo").getString("photo_100");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}