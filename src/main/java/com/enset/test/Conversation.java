package com.enset.test;

import java.sql.Timestamp;

public class Conversation {
    private final int id;
    private final String title;
    private final Timestamp createdAt;

    public Conversation(int id, String title, Timestamp createdAt) {
        this.id = id;
        this.title = title;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public Timestamp getCreatedAt() { return createdAt; }
}
