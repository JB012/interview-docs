package com.interviewdocs.server.model;
import java.net.URLEncoder;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;

@Entity
@Table (name = "videos")
public class Video {
    private @Id
    @GeneratedValue Long id;
    @JsonProperty("source")
    private String source;
    @JsonProperty("title")
    private String title;
    @JsonProperty("user_id")
    private String userId;
    
    Video() {}

    Video(String title) {
        this.title = title;
    }
    
    Video(String title, String source) {
        this.title = title;
        this.source = source;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getTitle() {
        return title;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSource() {
        return source;
    }

    public String getKeyName() {
        return userId + "/" + title;
    }

    @Override
    public String toString() {
        return "Video is named " + this.title;
    }
}