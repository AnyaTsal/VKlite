package com.vklite.model;

public class Message {
    private String message;
    private String time;
    private User sender;

    public Message(String message, String time, User sender) {
        this.message = message;
        this.time = time;
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public String getTime() {
        return time;
    }

    public User getSender() {
        return sender;
    }
}
