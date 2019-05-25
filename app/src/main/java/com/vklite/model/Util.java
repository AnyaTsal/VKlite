package com.vklite.model;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Util {

    public static final String executeCodeNotifications =
            "var longPull = API.messages.getLongPollHistory({\"ts\":  %s, \"pts\": %s" +
            "});" +
            "var messages = [];" +
            "var  i=0;" +
            "var new_pts = longPull.new_pts;" +
            "var result = [];" +
            "messages = longPull.messages.items;" +
            "while(i < messages.length) {" +
            "if(messages[i].out == 0) { var temp = messages[i];" +
            "if(messages[i].chat_id == null) {" +
            "var name = API.users.get({\"user_ids\" : messages[i].user_id, \"fields\": \"photo_100\"});" +
            "temp = messages[i] + name[0];" +
            "}" +
            "result.push(temp);" +
            "}" +
            "i = i + 1;" +
            "}" +
            "return {\"messages\" : result, \"new_pts\": new_pts};";

    public static final String longPullServerRequest = "https://%s?act=a_check&key=%s&ts=%s&wait=25&mode=96&version=3";

    public static String convertTime(long unixTime) {
        Date date = new Date(unixTime * 1000L);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("d MMM HH:mm");
        return sdf.format(date);
    }
}