package com.interviewdocs.server.model;
import jakarta.persistence.*;

@Entity
@Table (name = "videos")
public class Video {
    private @Id
    @GeneratedValue Long id;
    private String title;
    private String userId;
    
    Video() {}

    Video(String title) {
        this.title = title;
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

    @Override
    public String toString() {
        return "Video is named " + this.title;
    }
}