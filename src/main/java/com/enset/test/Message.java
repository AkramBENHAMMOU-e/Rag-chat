package com.enset.test;

import java.sql.Timestamp;

// Classes de modèle
public class Message {
    private final String text;
    private final boolean isUser;
    private final Timestamp timestamp;

    public Message(String text, boolean isUser, Timestamp timestamp) {
        this.text = text;
        this.isUser = isUser;
        this.timestamp = timestamp;
    }

    public String getText() { return text; }
    public boolean isUser() { return isUser; }
    public Timestamp getTimestamp() { return timestamp; }
}
