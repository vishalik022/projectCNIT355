package com.example.dreamanalyzer;

import java.util.Map;

public class Dream {
    private String id;
    private String userId;
    private String text;
    private long timestamp;
    private String mood; // e.g., "happy", "anxious"
    private Map<String, Object> aiTags; // e.g., {"sentiment":"negative", "themes":["falling","chase"]}

    public Dream() {} // empty constructor for Firestore

    public Dream(String id, String userId, String text, long timestamp, String mood, Map<String, Object> aiTags) {
        this.id = id;
        this.userId = userId;
        this.text = text;
        this.timestamp = timestamp;
        this.mood = mood;
        this.aiTags = aiTags;
    }

    // getters & setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public String getMood() { return mood; }
    public void setMood(String mood) { this.mood = mood; }
    public Map<String, Object> getAiTags() { return aiTags; }
    public void setAiTags(Map<String, Object> aiTags) { this.aiTags = aiTags; }
}
