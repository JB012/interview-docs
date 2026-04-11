package com.interviewdocs.server.model;
import jakarta.persistence.*;

@Entity
@Table (name = "videos")
public class Video {
    private @Id
    @GeneratedValue Long id;
    private String title;
    
    Video() {}

    Video(String title) {
        this.title = title;
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