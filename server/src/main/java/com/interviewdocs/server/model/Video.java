package com.interviewdocs.server.model;
import jakarta.persistence.*;

@Entity
@Table (name = "videos")
public class Video {
    private @Id
    @GeneratedValue Long id;
    private String source;
    private String title;
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

    public void setVideoTitle(String title) {
        this.title = title;
    }
    
    public String getVideoTitle() {
        return title;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSource() {
        return source;
    }

    @Override
    public String toString() {
        return "Video is named " + this.title;
    }
}